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
    
    @Inject(method = "render", at = @At(("HEAD")))
    private void render(MatrixStack matrixStack, float tickDelta, CallbackInfo ci) {
        // ここはクライアントサイドかサーバーサイドかというと、クライアントサイドです。
        int x = 0;
        int y = 0;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();
//            FantalModCommand.notifyAllPlayers(client.getServer(), "width: " + width + ", height: " + height);
            
            x = width / 2;
            y = height;
        }
        
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, EMPTY);
        DrawableHelper.drawTexture(matrixStack, x - 94, y - 54, 0, 0, 91, 2, 12, 12);
        
        RenderSystem.setShaderTexture(0, FULL);
        if (5 > 1) {
            DrawableHelper.drawTexture(matrixStack, x - 94, y - 50, 0, 0, 182, 5, 182, 5);
        }
        
        // playerを取得したい。client.playerはnullだった。
        
        var pollution = FantalStateManager.getPlayerState(client.player).getFantalPollution();
        
        RenderSystem.setShaderTexture(0, PIC);
        DrawableHelper.drawTexture(matrixStack, 0, 0, 0, 0, pollution * 2, 123, 386, 123);
    }
}
