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
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.registry.FantalModItems;
import org.kgcc.fantalmod.registry.FantalModSkills;
import org.kgcc.fantalmod.tool.FantalTool;

import java.util.ArrayList;
import java.util.List;

public class FantalBenchScreen extends HandledScreen<FantalBenchScreenHandler> {
    private static final Identifier TEXTURE =
            new Identifier(FantalMod.MODID, "textures/gui/siroan_block.png");
    
    public FantalBenchScreen(FantalBenchScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
    
    private static final int BUTTON_HEIGHT = 10;
    private static final int VISIBLE_NUM = 5;
    
    private int listX() {
        return x + 45;// todo: 位置調整
    }
    
    private int listY() {
        return y;
    }
    
    private final List<ButtonWidget> allButtons = new ArrayList<>();
    private int scrollOffset = 0;
    private boolean isDraggingScrollbar = false;
    private int scrollbarTop, scrollbarHeight, scrollbarBarHeight;
    
    
    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;

//        addDrawableChild(
//                ButtonWidget.builder(Text.of("Enable Sword Effect"), button -> {
//                                Slot slot = handler.getSlot(1);
//                                if (slot.hasStack() &&
//                                        slot.getStack().getItem() == FantalModItems.RED_SMALL) {
//                                    FantalStateManager.setSwordEffectEnabled(true);
//                                    // サーバーにデータを送信（SiroanBlockScreenHandler.OnButtonClickが作動する。idは0）
//                                    client.interactionManager.clickButton(handler.syncId, 0);
//                                    FantalMod.LOGGER.info("Sword effect enabled!");
//                                } else {
//                                    FantalMod.LOGGER.info("Failed to enable sword effect.");
//                                }
//                            }).size(8, 10)
//                            .width(80)
//                            .tooltip(Tooltip.of(Text.of("このボタンを押すとred_smallを消費します")))
//                            .position(200, 60)
//                            .build());
        
        allButtons.clear();
        
        int idx = 0;
        for (String name : FantalModSkills.SKILLS.keySet()) {
            int finalIdx = idx;
            ButtonWidget button = ButtonWidget.builder(Text.literal(name), b -> {
                Slot slot = handler.getSlot(1);
                if (!slot.hasStack() ||
                        !(slot.getStack().getItem() == FantalModItems.RED_SMALL)) {
                    FantalMod.LOGGER.info("Failed to change skill.");
                    return;
                }
                // サーバーにデータを送信（SiroanBlockScreenHandler.OnButtonClickが作動する。idはidx）
                client.interactionManager.clickButton(handler.syncId, finalIdx);
                
//                Slot toolSlot = handler.getSlot(0);
//                if (!toolSlot.hasStack() || !(toolSlot.getStack().getItem() instanceof FantalTool tool)) {
//                    FantalMod.LOGGER.info("No valid tool in slot 0.");
//                    return;
//                }
//                tool.setSkill(name);  // ツールのスキルを変更
                
                FantalMod.LOGGER.info("Skill changed!");
            }).dimensions(listX() + 10, listY() + 20 + idx * BUTTON_HEIGHT, 100, BUTTON_HEIGHT).build();
            allButtons.add(button);
            idx++;
        }
        
        updateVisibleButtons();  // 最初に表示する分だけ追加
    }
    
    
    private void updateVisibleButtons() {
        this.clearChildren(); // remove old buttons
        int start = scrollOffset;
        int end = Math.min(scrollOffset + VISIBLE_NUM, allButtons.size());
        
        for (int i = start; i < end; i++) {
            ButtonWidget button = allButtons.get(i);
            int visualIndex = i - scrollOffset;
            button.setY(listY() + 20 + visualIndex * BUTTON_HEIGHT);
            this.addDrawableChild(button); // これで描画っぽい
        }
//        FantalMod.LOGGER.info("x:{},y:{}", x, y);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int maxOffset = Math.max(0, allButtons.size() - VISIBLE_NUM);
        // offsetをamount（引数、スクロール量）に応じて変更し、0~maxOffsetの範囲に収める
        scrollOffset = MathHelper.clamp(scrollOffset - (int) amount, 0, maxOffset);
        updateVisibleButtons();
        return true;
    }
    
    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        
        // スクロールバーの描画（オプション）
        drawScrollbar(matrices);
    }
    
    private void drawScrollbar(MatrixStack matrices) {
        int contentSize = allButtons.size();
        if (contentSize <= VISIBLE_NUM)
            return;
        
        scrollbarHeight = BUTTON_HEIGHT * VISIBLE_NUM;  // 全体の高さ
        scrollbarTop = listY() + 20;  // スクロールバーの起点位置
        
        double scrollbarRatio =
                VISIBLE_NUM < contentSize ? (1 - (contentSize - VISIBLE_NUM) / (double) contentSize) : 1;
        // スクロールバーの高さを計算
        scrollbarBarHeight = MathHelper.clamp((int) (scrollbarHeight * scrollbarRatio), 3, scrollbarHeight);
        
        int barY = scrollbarTop + scrollOffset * (scrollbarHeight - scrollbarBarHeight) / (contentSize - VISIBLE_NUM);
        int scrollbarBottom =
                scrollbarTop + contentSize * (scrollbarHeight - scrollbarBarHeight) / (contentSize - VISIBLE_NUM);
        
        // 描画
        fill(matrices, listX() + 120, scrollbarTop, listX() + 125, scrollbarBottom, 0xFF777777);
        fill(matrices, listX() + 120, barY, listX() + 125, barY + scrollbarBarHeight + 1, 0xFFAAAAAA);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOverScrollbar(mouseX, mouseY)) {
            isDraggingScrollbar = true;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDraggingScrollbar = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDraggingScrollbar) {
            int contentSize = allButtons.size();
            int maxScroll = contentSize - VISIBLE_NUM;
            
            double mouseRelative = mouseY - scrollbarTop;
            double ratio = mouseRelative / (scrollbarHeight - scrollbarBarHeight);
            scrollOffset = MathHelper.clamp((int) (ratio * maxScroll), 0, maxScroll);
            
            updateVisibleButtons();
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    private boolean isMouseOverScrollbar(double mouseX, double mouseY) {
        return mouseX >= listX() + 120 && mouseX <= listX() + 125 &&
                mouseY >= scrollbarTop && mouseY <= scrollbarTop + scrollbarHeight;
    }
    
}
