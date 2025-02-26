package org.kgcc.fantalmod.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.registry.FantalModStatusEffects;
import org.kgcc.fantalmod.util.FantalStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow
    private int scaledHeight;
    @Shadow
    private int scaledWidth;
    // @Uniqueをつけておくと、Mixinが適用されるクラスに同名のフィールドが無いことが保証される。
    // もしあった場合、コンパイル時にエラーが発生する。
    @Unique
    private static final Identifier FULL = new Identifier(FantalMod.MODID, "textures/test/experience_bar_progress.png");
    @Unique
    private static final Identifier EMPTY = new Identifier(FantalMod.MODID,
                                                           "textures/test/experience_bar_background.png");
    @Unique
    private static final Identifier PIC = new Identifier(FantalMod.MODID, "textures/test/pic.png");
    @Unique
    private static final int PIC_WIDTH = 386;
    @Unique
    private static final int PIC_HEIGHT = 123;
    
    @Unique
    private static final int EFFECT_WIDTH = 18;
    @Unique
    private static final int EFFECT_HEIGHT = 18;
    @Unique
    private static final Identifier HUNGER = new Identifier(FantalMod.MODID, "textures/mob_effect/hunger.png");
    
    @Unique
    private static final Identifier PIC2 = new Identifier(FantalMod.MODID, "textures/mob_effect/320.jpg");
    
    // ゲージ
    @Unique
    private static final Identifier GAUGE = new Identifier(FantalMod.MODID, "textures/test/gauge.png");
    
    @Unique
    private static final Identifier GAUGE1 = new Identifier(FantalMod.MODID, "textures/test/gauge1.png");
    
    @Unique
    private static final Identifier GAUGE2 = new Identifier(FantalMod.MODID, "textures/test/gauge2.png");
    
    @Unique
    private static final Identifier BA_N = new Identifier(FantalMod.MODID, "textures/test/ba-n3.jpg");
    
    @Inject(method = "render", at = @At(("HEAD")))
    private void render(MatrixStack matrixStack, float tickDelta, CallbackInfo ci) {
        // ここはクライアントサイドかサーバーサイドかというと、クライアントサイドです。
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            FantalMod.LOGGER.error("render Mixin:MinecraftClient is null");
            return;
        }
        
        // ウィンドウの幅と高さを取得
        int scaledWidth = client.getWindow().getScaledWidth();
        int mid = scaledWidth / 2;
        int scaledHeight = client.getWindow().getScaledHeight();
//        FantalModCommand.notifyAllPlayers(client.getServer(), "width: " + scaledWidth + ", height: " + scaledHeight);
        
        // テクスチャのバインド
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        
        // 鉱石病のHUD
        if (client.player != null && client.player.hasStatusEffect(FantalModStatusEffects.ORIPATHY)) {
            RenderSystem.setShaderTexture(0, PIC2);
            var size = 320 / 3;
            var size2 = 320 / 4;
            DrawableHelper.drawTexture(matrixStack, 0, 0, 0, 0, size, size, size, size);
            DrawableHelper.drawTexture(matrixStack, size, 0, 0, 0, size2, size2, size2, size2);
        }
        
        
        final int mode = 5;
        switch (mode) {
            case 0 -> {
                RenderSystem.setShaderTexture(0, EMPTY);
                DrawableHelper.drawTexture(matrixStack, mid - 94, scaledHeight - 54, 0, 0, 91, 2, 12, 12);
                
                RenderSystem.setShaderTexture(0, FULL);
                if (5 > 1) {
                    DrawableHelper.drawTexture(matrixStack, mid - 94, scaledHeight - 50, 0, 0, 182, 5, 182, 5);
                }
            }
            case 1 -> {
                // 横向きのバー
                var pollution = FantalStateManager.getPlayerState(client.player).getFantalPollution();
                
                RenderSystem.setShaderTexture(0, PIC);
                DrawableHelper.drawTexture(matrixStack, 0, 0, 0, 0, pollution * 2, 123, PIC_WIDTH, PIC_HEIGHT);
            }
            case 2 -> {
                // 縦向きのバー
                var pollution = FantalStateManager.getPlayerState(client.player).getFantalPollution();
                
                RenderSystem.setShaderTexture(0, PIC);
                final int pollution_max = 200;
                final int pos = pollution_max / 2 - pollution / 2;
                final int height = pollution / 2 + (PIC_HEIGHT - pollution_max / 2);
                DrawableHelper.drawTexture(matrixStack, 0, pos, 0, pos, PIC_WIDTH, height, PIC_WIDTH, PIC_HEIGHT);
            }
            case 3 -> {
                RenderSystem.setShaderTexture(0, HUNGER);
                DrawableHelper.drawTexture(matrixStack, 0, 0, 0, 0, EFFECT_WIDTH * 2, EFFECT_HEIGHT * 2, EFFECT_WIDTH,
                                           EFFECT_HEIGHT);
                
                
                int red = 0xFFFF0000; // 赤色 (ARGB形式)
                
                // ----- ホットバーの領域の描画 -----
                int hotBarWidth = 182;
                int hotBarHeight = 91;
                
                DrawableHelper.fill(matrixStack, mid - hotBarWidth / 2, scaledHeight - hotBarHeight / 2,
                                    mid + hotBarWidth / 2, scaledHeight, red);
                // ----- ホットバーの領域の描画終わり -----
                
                // 0からhotBarLeftの間にバーを描画できる
                int hotBarLeft = mid - hotBarWidth / 2;
                FantalMod.LOGGER.info("hotBarLeft: " + hotBarLeft);


//                // 赤, 緑, 青, 黄, 紫, 水色
//                List<Integer> color_list = List.of(0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFFFFFF00, 0xFFFF00FF,
//                                                   0xFF00FFFF);
//                int y = 200;
//                for (int i = 0; i < 140; i += 10) {
//                    DrawableHelper.fill(matrixStack, i, y, i + 10, y + 10, color_list.get(i % color_list.size()));
//                }
            }
            case 4 -> {
                RenderSystem.setShaderTexture(0, PIC);
                DrawableHelper.drawTexture(matrixStack, 0, 0, 0, 0, 386 / 4, 123, 386 / 4, 123);
            }
            case 5 -> {
                // ゲージ試作
                RenderSystem.setShaderTexture(0, GAUGE1);
                
                // 320, 214
                
                // バーの計算
                int hotBarWidth = 182;
                int hotBarHeight = 91;
                int hotBarLeft = mid - hotBarWidth / 2;
                
                int gaugeWidth = 50;
                int gaugeHeight = 100;
                
                int scaledGaugeWidth = gaugeWidth * scaledWidth / 320 * 4 / 5;
                int scaledGaugeHeight = gaugeHeight * scaledWidth / 320 * 4 / 5;
                
                DrawableHelper.drawTexture(matrixStack, (hotBarLeft - scaledGaugeWidth) / 2,
                                           scaledHeight - 5 - scaledGaugeHeight, 0, 0, scaledGaugeWidth,
                                           scaledGaugeHeight, scaledGaugeWidth, scaledGaugeHeight);
                
                int currentHeight = scaledGaugeHeight - (int) client.world.getTime() % scaledGaugeHeight;
                
                RenderSystem.setShaderTexture(0, GAUGE2);
                DrawableHelper.drawTexture(matrixStack, (hotBarLeft - scaledGaugeWidth) / 2,
                                           scaledHeight - 5 - scaledGaugeHeight, 0, 0, scaledGaugeWidth, currentHeight,
                                           scaledGaugeWidth, scaledGaugeHeight);
                
                
            }
            case 6 -> {
                RenderSystem.setShaderTexture(0, BA_N);
                DrawableHelper.drawTexture(matrixStack, 0, 0, 0, 0, scaledWidth, scaledHeight, scaledWidth,
                                           scaledHeight);
            }
        }
    }
}
