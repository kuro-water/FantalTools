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
        int scaledGaugeHeight = gaugeHeight * screenHeight / 320 * 4 / 5;

        //ゲージの中身の位置調整
        int Gaugesetx = (80/12) * screenWidth / 320 * 4/ 5;
        int Gaugesety = (240/12) * screenHeight / 320 * 4 / 5;

        //ゲージの中身のサイズ
        int textureWidth = scaledGaugeWidth * 220 / 600;
        int textureHeight = scaledGaugeHeight * 760 / 1200;

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

        //感染度の%に変換(下のをコメントアウトして上のコメントアウト外す)
        //int pollution = FantalStateManager.getPlayerState(player).getFantalPollution();
        int pollution = (int) (client.world.getTime() % 200);

        //(client.world.getTime()をpollutionにすればいける…はず)
        int currentHeight = (int) client.world.getTime() % textureHeight;
        int currentLength = textureHeight - currentHeight;

        if(currentHeight == 1)
        {
            client.player.sendMessage(Text.literal(""+textureHeight));
        }

        // ゲージの中身の描画
        RenderSystem.setShaderTexture(0, NAKAMI);
        y += currentLength;
        DrawableHelper.drawTexture(
                matrixStack, x + Gaugesetx, y + Gaugesety, 0, currentLength, textureWidth, currentHeight,
                textureWidth, textureHeight);
        /*
        y += currentLength;
        DrawableHelper.drawTexture(
                matrixStack, x + 5, y - 9, 0, currentLength + 10, textureWidth, currentHeight,
                textureWidth, textureHeight);
        */
    }
}