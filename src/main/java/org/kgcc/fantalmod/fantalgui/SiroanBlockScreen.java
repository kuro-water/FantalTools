//========================================================
// FantalMod - Minecraft Mod
//　うん
//　
//
//
//===========================================================
package org.kgcc.fantalmod.fantalgui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.registry.FantalModItems;
import org.kgcc.fantalmod.util.FantalStateManager;

public class SiroanBlockScreen extends HandledScreen<SiroanBlockScreenHandler> {
    private static final Identifier TEXTURE =
            new Identifier(FantalMod.MODID, "textures/gui/siroan_block.png");
    
    public SiroanBlockScreen(SiroanBlockScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
    
    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;

        addDrawableChild(
                ButtonWidget.builder(Text.of("Enable Sword Effect"), button -> {
                                Slot slot = handler.getSlot(1);
                                if (slot.hasStack() &&
                                        slot.getStack().getItem() == FantalModItems.RED_SMALL) {
                                    FantalStateManager.setSwordEffectEnabled(true);
                                    // サーバーにデータを送信（SiroanBlockScreenHandler.OnButtonClickが作動する。idは0）
                                    client.interactionManager.clickButton(handler.syncId, 0);
                                    FantalMod.LOGGER.info("Sword effect enabled!");
                                } else {
                                    FantalMod.LOGGER.info("Failed to enable sword effect.");
                                }
                            }).size(8, 10)
                            .width(80)
                            .tooltip(Tooltip.of(Text.of("このボタンを押すとred_smallを消費します")))
                            .position(200, 60)
                            .build());
    }
    
    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        
    }
}
