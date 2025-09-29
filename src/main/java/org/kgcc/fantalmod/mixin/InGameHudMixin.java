package org.kgcc.fantalmod.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.ConfigButtonScreen;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.FantalModState;
import org.kgcc.fantalmod.util.FantalStateManager;
import org.kgcc.fantalmod.util.PlayerFantalData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin{
    @Shadow @Final private MinecraftClient client;

    @Unique
    private static final Identifier WAKU = new Identifier(FantalMod.MODID, "textures/gui/waku.png");

    @Unique
    private static final Identifier NAKAMI = new Identifier(FantalMod.MODID, "textures/gui/nakami.png");

    @Unique
    private static final Identifier AIKON = new Identifier(FantalMod.MODID, "textures/gui/aikon.png");

    @Inject(method = "render", at = @At(("HEAD")))
    private void render(MatrixStack matrixStack, float tickDelta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!FantalModState.isShowImage()) return;

        int x = 0;
        int y = 0;
        int cy = 0;

        int imageWidth = 64;
        int imageHeight = 128;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledWidth();

        switch (FantalModState.getImagePosition()) {
            case TOP_LEFT -> {
                x = 0;
                y = 0;
            }
            case TOP_RIGHT -> {
                x = screenWidth - imageWidth + 5;
                y = screenHeight / 2 - imageHeight - 85;
            }
            case BOTTOM_LEFT -> {
                x = 0;
                y = screenHeight / 2 - imageHeight + 70;
            }
            case BOTTOM_RIGHT -> {
                x = screenWidth - imageWidth + 5;
                y = screenHeight / 2 - imageHeight + 70;
            }
            case CUSTOM -> {
                x = FantalModState.getCustomX();
                y = FantalModState.getCustomY();
            }
        }

        if (client == null) {
            // そんなことはありえないはず
            FantalMod.LOGGER.error("render Mixin:MinecraftClient is null");
            return;
        }

        int mid = screenWidth / 2;

        // テクスチャのバインド
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        //final int hotBarWidth = 182;
        //final int hotBarHeight = 91;
        //final int hotBarLeft = mid - hotBarWidth / 2;

        // ゲージの基準サイズ（比率）
        int gaugeWidth = 50;
        int gaugeHeight = 100;

        // ゲージの実サイズ
        int scaledGaugeWidth = gaugeWidth * screenWidth / 320 * 4 / 5;
        int scaledGaugeHeight = gaugeHeight * screenWidth / 320 * 4 / 5;

        //ゲージの中身のサイズ
        int textureWidth = gaugeWidth * screenWidth / 310 * 3 / 10 ;
        int textureHeight = gaugeHeight * screenWidth / 320 * 7 / 10;

        // ゲージの枠の描画
        RenderSystem.setShaderTexture(0, WAKU);
        DrawableHelper.drawTexture(
                matrixStack, x, y, 0, 0, scaledGaugeWidth, scaledGaugeHeight,
                scaledGaugeWidth, scaledGaugeHeight);

        //アイコンの描画
        RenderSystem.setShaderTexture(0, AIKON);
        DrawableHelper.drawTexture(
                matrixStack, x, y, 0, 0, scaledGaugeWidth, scaledGaugeHeight,
                scaledGaugeWidth, scaledGaugeHeight);
        PlayerEntity player = client.player;
        if (player == null) {
            FantalMod.LOGGER.error("render Mixin:PlayerEntity is null");
            return;
        }

        //int pollution = FantalStateManager.getPlayerState(player).getFantalPollution();
        //int pollution = (int) (client.world.getTime() % scaledGaugeHeight);
        int pollution = (int) (client.world.getTime() % 200);
/*
        // 高さの計算
        int currentHeight = pollution / 2;
        int currentLength =  scaledGaugeHeight - ((scaledGaugeHeight * currentHeight) / 100);

        if(currentHeight == 0 || currentHeight == 25 || currentHeight == 75 || currentHeight == 100){
            client.player.sendMessage(Text.literal("浸食度:" + pollution + "a:" + ((scaledGaugeHeight * currentHeight) / 100)));
        }

        // ゲージの中身の描画
        RenderSystem.setShaderTexture(0, NAKAMI);
        int textureWidth = 32;   // 実際のnakami.pngの幅
        int textureHeight = 128;  // 実際のnakami.pngの高さ

        y = currentLength;
        DrawableHelper.drawTexture(
                matrixStack, x, y, 0, textureHeight - currentHeight, scaledGaugeWidth / 2, currentHeight,
                textureWidth, textureHeight);
*/
        int currentHeight = (int) client.world.getTime() % scaledGaugeHeight;
        int currentLength = scaledGaugeHeight - currentHeight;



        // ゲージの中身の描画
        RenderSystem.setShaderTexture(0, NAKAMI);
        y += currentLength;
        DrawableHelper.drawTexture(
                matrixStack, x + 5, y - 9, 0, currentLength, (int)(scaledGaugeWidth * 0.366f), (int)(scaledGaugeWidth * 0.366f),
                220, 760);
    }

    /*
    @Inject(method = "render", at = @At("HEAD"))
    private void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (!FantalModState.isShowImage()) return;

        int x = 0;
        int y = 0;

        int imageWidth = 128;
        int imageHeight = 256;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledWidth();

        switch (FantalModState.getImagePosition()) {
            case TOP_LEFT -> {
                x = 5;
                y = 5;
            }
            case TOP_RIGHT -> {
                x = screenWidth - imageWidth - 5;
                y = 5;
            }
            case BOTTOM_LEFT -> {
                x = 5;
                y = screenHeight / 2 - imageHeight - 5;
            }
            case BOTTOM_RIGHT -> {
                x = screenWidth - imageWidth - 5;
                y = screenHeight / 2 - imageHeight - 5;
            }
            case CUSTOM -> {
                x = FantalModState.getCustomX();
                y = FantalModState.getCustomY();
            }
        }

        RenderSystem.enableBlend(); // ブレンドを有効にする
        RenderSystem.defaultBlendFunc(); // 標準のアルファブレンド関数を設定
        RenderSystem.setShaderTexture(0, IMAGE);
        RenderSystem.setShaderTexture(0, NAKAMI);
        DrawableHelper.drawTexture(matrices, x, y, 0, 0, imageWidth, imageHeight, imageHeight, imageHeight);
    }
     */
}







//package org.kgcc.fantalmod.mixin;
//
//import com.mojang.blaze3d.systems.RenderSystem;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.gui.DrawableHelper;
//import net.minecraft.client.gui.hud.InGameHud;
//import net.minecraft.client.render.GameRenderer;
//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.util.Identifier;
//import org.kgcc.fantalmod.FantalMod;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(InGameHud.class)
//public class InGameHudMixin {
//    @Shadow
//    private int scaledHeight;
//    @Shadow
//    private int scaledWidth;
//    // @Uniqueをつけておくと、Mixinが適用されるクラスに同名のフィールドが無いことが保証される。
//    // もしあった場合、コンパイル時にエラーが発生する。
//    @Unique
//    private static final Identifier WAKU = new Identifier(FantalMod.MODID, "textures/test/waku.png");
//
//    @Unique
//    private static final Identifier NAKAMI = new Identifier(FantalMod.MODID, "textures/test/nakami.png");
//
//    @Inject(method = "render", at = @At(("HEAD")))
//    private void render(MatrixStack matrixStack, float tickDelta, CallbackInfo ci) {
//        MinecraftClient client = MinecraftClient.getInstance();
//        if (client == null) {
//            // そんなことはありえないはず
//            FantalMod.LOGGER.error("render Mixin:MinecraftClient is null");
//            return;
//        }
//
//        // ウィンドウの幅と高さを取得
//        int scaledWidth = client.getWindow().getScaledWidth();
//        int mid = scaledWidth / 2;
//        int scaledHeight = client.getWindow().getScaledHeight();
////        FantalModCommand.notifyAllPlayers(client.getServer(), "width: " + scaledWidth + ", height: " + scaledHeight);
//
//        // テクスチャのバインド
//        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//
//        final int hotBarWidth = 182;
//        final int hotBarHeight = 91;
//        final int hotBarLeft = mid - hotBarWidth / 2;
//
//        // ゲージの基準サイズ（比率）
//        int gaugeWidth = 50;
//        int gaugeHeight = 100;
//
//        // ゲージの実サイズ
//        int scaledGaugeWidth = gaugeWidth * scaledWidth / 320 * 4 / 5;
//        int scaledGaugeHeight = gaugeHeight * scaledWidth / 320 * 4 / 5;
//
//        // ゲージの枠の描画
//        RenderSystem.setShaderTexture(0, WAKU);
//        // 左端
//        int x = (hotBarLeft - scaledGaugeWidth) / 2;
//        // 上端
//        int y = scaledHeight - 5 - scaledGaugeHeight;
//        DrawableHelper.drawTexture(
//                matrixStack, x, y, 0, 0, scaledGaugeWidth, scaledGaugeHeight,
//                scaledGaugeWidth, scaledGaugeHeight);
//
//        // 高さの計算
//        int currentHeight = (int) client.world.getTime() % scaledGaugeHeight;
//        int currentLength = scaledGaugeHeight - currentHeight;
//
//        // ゲージの中身の描画
//        RenderSystem.setShaderTexture(0, NAKAMI);
//        y += currentLength;
//        DrawableHelper.drawTexture(
//                matrixStack, x, y, 0, currentLength, scaledGaugeWidth, currentHeight,
//                scaledGaugeWidth, scaledGaugeHeight);
//    }
//}