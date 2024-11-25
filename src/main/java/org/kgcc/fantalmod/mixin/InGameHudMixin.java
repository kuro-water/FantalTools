package org.kgcc.fantalmod.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.util.FantalStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Unique
    private static final Identifier FULL = new Identifier(FantalMod.MODID, "textures/test/experience_bar_progress.png");
    @Unique
    private static final Identifier EMPTY = new Identifier(FantalMod.MODID,
                                                           "textures/test/experience_bar_background.png");
    @Unique
    private static final Identifier PIC = new Identifier(FantalMod.MODID, "textures/test/pic.png");
    private static final int PIC_WIDTH = 386;
    private static final int PIC_HEIGHT = 123;
    
    @Inject(method = "render", at = @At(("HEAD")))
    private void render(MatrixStack matrixStack, float tickDelta, CallbackInfo ci) {
        // ここはクライアントサイドかサーバーサイドかというと、クライアントサイドです。
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            FantalMod.LOGGER.error("render Mixin:MinecraftClient is null");
            return;
        }
        
        int x = client.getWindow().getScaledWidth() / 2;
        int y = client.getWindow().getScaledHeight();
//            FantalModCommand.notifyAllPlayers(client.getServer(), "width: " + width + ", height: " + height);
        
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, EMPTY);
        DrawableHelper.drawTexture(matrixStack, x - 94, y - 54, 0, 0, 91, 2, 12, 12);
        
        RenderSystem.setShaderTexture(0, FULL);
        if (5 > 1) {
            DrawableHelper.drawTexture(matrixStack, x - 94, y - 50, 0, 0, 182, 5, 182, 5);
        }
        
        final int mode = 2;
        
        switch (mode) {
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
        }
    }
}
