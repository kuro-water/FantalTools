package org.kgcc.fantalmod.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.ConfigButtonScreen;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.FantalModState;
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
    private static final Identifier IMAGE = new Identifier(FantalMod.MODID, "textures/gui/experience_bar_background.png");

    @Inject(method = "render", at = @At("HEAD"))
    private void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (!FantalModState.isShowImage()) return;

        int x = 0;
        int y = 0;

        int imageWidth = 64;
        int imageHeight = 64;

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

        RenderSystem.setShaderTexture(0, IMAGE);
        DrawableHelper.drawTexture(matrices, x, y, 0, 0, imageWidth, imageHeight, imageHeight, imageHeight);
    }
}
