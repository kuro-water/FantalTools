//========================================================
// FantalMod - Minecraft Mod
//　うん
//　
//
//
//===========================================================
package org.kgcc.fantalmod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.registry.FantalModItems;
import org.kgcc.fantalmod.skill.BlinkSkill;
import org.kgcc.fantalmod.tool.FantalTool;

public class FantalBenchScreen extends HandledScreen<FantalBenchScreenHandler> {
    private static final Identifier TEXTURE =
            new Identifier(FantalMod.MODID, "textures/gui/siroan_block.png");
    
    public FantalBenchScreen(FantalBenchScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
    
    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        
        addDrawableChild(
                ButtonWidget
                        .builder(Text.of("Enable Sword Effect"), button -> {
                            if (client == null || client.interactionManager == null) {
                                FantalMod.LOGGER.error("Client or interaction manager is null.");
                                return;
                            }
                            
                            Item item = handler.getSlot(0).getStack().getItem();
                            if (!(item instanceof FantalTool fantalItem)) {
                                FantalMod.LOGGER.error("Item is not a FantalTool.");
                                return;
                            }
                            
                            if (!(handler.getSlot(1).getStack().getItem() == FantalModItems.RED_SMALL)) {
                                FantalMod.LOGGER.info("Need red_small.");
                                return;
                            }
                            
                            // サーバーにデータを送信（SiroanBlockScreenHandler.OnButtonClickが作動する。idは0）
                            client.interactionManager.clickButton(handler.syncId, 0);
                            // todo:どのスキルを設定するか
                            fantalItem.setSkill(new BlinkSkill());
                            FantalMod.LOGGER.info("Skill enabled!");
                        })
                        .size(8, 10)
                        .width(80)
                        .tooltip(Tooltip.of(Text.of("red_smallを消費して特殊効果を有効化")))
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
