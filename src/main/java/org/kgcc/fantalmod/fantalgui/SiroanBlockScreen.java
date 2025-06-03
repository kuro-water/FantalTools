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
import net.minecraft.util.math.MathHelper;
import org.kgcc.fantalmod.FantalMod;

import java.util.ArrayList;
import java.util.List;

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
        
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        allButtons.clear();
        
        // 仮の50個のボタンを作成
        for (int i = 0; i < 50; i++) {
            int index = i;
            ButtonWidget btn = ButtonWidget.builder(Text.literal("Button #" + i), b -> {
                System.out.println("Clicked: " + index);
            }).dimensions(x + 10, y + 20 + i * BUTTON_HEIGHT, 100, BUTTON_HEIGHT).build();
            allButtons.add(btn);
        }
        
        updateVisibleButtons();  // 最初に表示する分だけ追加
    }
    
    private static final int BUTTON_HEIGHT = 20;
    private static final int VISIBLE_COUNT = 6;
    
    private final List<ButtonWidget> allButtons = new ArrayList<>();
    private int scrollOffset = 0;
    private boolean isDraggingScrollbar = false;
    private int scrollbarTop, scrollbarHeight, scrollbarBarHeight;
    
    
    private void updateVisibleButtons() {
        this.clearChildren(); // remove old buttons
        int start = scrollOffset;
        int end = Math.min(scrollOffset + VISIBLE_COUNT, allButtons.size());
        
        for (int i = start; i < end; i++) {
            ButtonWidget button = allButtons.get(i);
            int visualIndex = i - scrollOffset;
            button.setY(y + 20 + visualIndex * BUTTON_HEIGHT);
            this.addDrawableChild(button);
        }
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int maxOffset = Math.max(0, allButtons.size() - VISIBLE_COUNT);
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
        if (contentSize <= VISIBLE_COUNT)
            return;
        
        scrollbarHeight = 120;  // 全体の高さ
        scrollbarTop = y + 20;  // スクロールバーの起点位置
        scrollbarBarHeight = scrollbarHeight * VISIBLE_COUNT / contentSize;
        
        int barY = scrollbarTop + scrollOffset * (scrollbarHeight - scrollbarBarHeight) / (contentSize - VISIBLE_COUNT);
        
        // 描画
        fill(matrices, x + 120, barY, x + 125, barY + scrollbarBarHeight, 0xFFAAAAAA);
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
            int maxScroll = contentSize - VISIBLE_COUNT;
            
            double mouseRelative = mouseY - scrollbarTop;
            double ratio = mouseRelative / (scrollbarHeight - scrollbarBarHeight);
            scrollOffset = MathHelper.clamp((int) (ratio * maxScroll), 0, maxScroll);
            
            updateVisibleButtons();
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    private boolean isMouseOverScrollbar(double mouseX, double mouseY) {
        return mouseX >= x + 120 && mouseX <= x + 125 &&
                mouseY >= scrollbarTop && mouseY <= scrollbarTop + scrollbarHeight;
    }
    
}
