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
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.fantalgui.SiroanBlockScreenHandler;
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
        //一応保管
//        addDrawableChild(ButtonWidget.builder(Text.of("test_button"), button -> {
//            // インデックス1のスロットに石があるか確認
//            if (handler.getSlot(1).hasStack() &&
//                    handler.getSlot(1).getStack().getItem() == FantalModItems.RED_SMALL) {
//                // スロット1(index 1)のred_smallを1つ減らす
//                handler.getSlot(1).getStack().decrement(1);
//                FantalMod.LOGGER.info("hello world!!");
//            } else {
//                FantalMod.LOGGER.info("?????????????");
//            }
//        }).size(10, 10)
//                .width(100)
//                .position(86, 10)
//                .build());
        addDrawableChild(ButtonWidget.builder(Text.of("Enable Sword Effect"), button -> {
                    if (handler.getSlot(1).hasStack() &&
                            handler.getSlot(1).getStack().getItem() == FantalModItems.RED_SMALL) {
                        handler.getSlot(1).getStack().decrement(1);
                        FantalStateManager.setSwordEffectEnabled(true);
                        FantalMod.LOGGER.info("Sword effect enabled!");
                    } else {
                        FantalMod.LOGGER.info("Failed to enable sword effect.");
                    }
                }).size(10, 10)
                .width(100)
                .position(86, 10)
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



    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
        // ボタンの上にカーソルがある場合表示（ボタンと座標違うようなきがする
        if (isPointWithinBounds(86, 10, 100, 20, mouseX, mouseY)) {
            renderTooltip(matrices, Text.of("このボタンを押すとred_smallを消費します"), mouseX, mouseY+20);
        }

    }
}
