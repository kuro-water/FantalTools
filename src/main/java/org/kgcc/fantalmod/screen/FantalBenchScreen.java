//========================================================
// FantalMod - Minecraft Mod
//　うん
//　眠くなったらねる
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
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.registry.FantalModItems;
import org.kgcc.fantalmod.registry.FantalModSkills;
import org.kgcc.fantalmod.skill.BaseSkill;
import org.kgcc.fantalmod.tool.FantalToolItem;

import java.util.ArrayList;
import java.util.List;

public class FantalBenchScreen extends HandledScreen<FantalBenchScreenHandler> {
    private static final Identifier TEXTURE =
            new Identifier(FantalMod.MODID, "textures/gui/fantal_bench.png");
    // todo:GUIきれいにしないと。
    
    private static final int BUTTON_HEIGHT = 10;
    private static final int BUTTON_WIDTH = 100;
    private static final int VISIBLE_NUM = 5;
    
    
    private final List<ButtonWidget> allButtons = new ArrayList<>();
    private int scrollOffset = 0;
    private boolean isDraggingScrollbar = false;
    private int scrollbarTop, scrollbarHeight, scrollbarBarHeight;
    /**
     * <p>アイテム切り替わりの検知のための変数</p>
     *
     * @see #render(MatrixStack, int, int, float)
     */
    private Item presentItem;
    
    /**
     * リストのx座標。getter
     */
    private int getListX() {
        return x + 55;
    }
    
    /**
     * リストのy座標。getter
     */
    private int getListY() {
        return y + 20;
    }
    
    /**
     * {@link #init()}で生成する、ボタンの押下時のアクションを取得する関数<br>
     * idxをラムダ式のスコープに入れるために、変数ではなく関数で実装する。
     *
     * @param idx ボタンのインデックス
     * @return ボタンが押下時のアクション
     */
    private ButtonWidget.PressAction getPressAction(int idx) {
        // todo: idxだと対応しないスキルのせいでズレる
        return b -> {
            Slot slot = handler.getSlot(1);
            if (!slot.hasStack() ||
                    !(slot.getStack().getItem() == FantalModItems.RED_SMALL)) {
                FantalMod.LOGGER.info("No red small item in slot 1.");
                return;
            }
            if (client == null || client.interactionManager == null) {
                FantalMod.LOGGER.error("Client or interaction manager is null.");
                return;
            }
            
            // サーバーにデータを送信（FantalBenchScreenHandler.OnButtonClickが作動する。idはidx）
            client.interactionManager.clickButton(handler.syncId, idx);
            
            FantalMod.LOGGER.info("Skill changed!");
        };
    }
    
    @Override
    protected void init() {
        super.init();
        
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        
        updateVisibleButtons();  // 最初に表示する分だけ追加
    }
    
    public FantalBenchScreen(FantalBenchScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        FantalMod.LOGGER.info("render");
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
        
        Slot slot = handler.getSlot(0);
        Item item = slot.getStack().getItem();
        if (presentItem == item) {
            return;
        }
        // アイテムが変わったら再描画
        presentItem = item;
        allButtons.clear();
        
        if (!(item instanceof FantalToolItem)) {
            FantalMod.LOGGER.info("No valid tool in slot 0.");
            updateVisibleButtons();
            return;
        }
        
        int idx = 0;
        for (BaseSkill skill : FantalModSkills.SKILLS) {
            if (!skill.isToolSupported(item)) {
                continue;
            }
            ButtonWidget button = new CustomFontButtonWidget(
                    getListX(), getListY() + idx * BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT,
                    skill.getName(), getPressAction(idx), 0.9f, Tooltip.of(skill.getTooltip())
            );
            allButtons.add(button);
            idx++;
        }
        updateVisibleButtons();
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
    
    // === 描画関連（スクロールが主）===
    // スクロールはchatGPTとともに自力実装したので参考資料がありません
    private void updateVisibleButtons() {
        this.clearChildren(); // remove old buttons
        int start = scrollOffset;
        int end = Math.min(scrollOffset + VISIBLE_NUM, allButtons.size());
        
        for (int i = start; i < end; i++) {
            ButtonWidget button = allButtons.get(i);
            int visualIndex = i - scrollOffset;
            button.setY(getListY() + visualIndex * (BUTTON_HEIGHT));
            this.addDrawableChild(button); // これで描画っぽい
        }
    }
    
    private void drawScrollbar(MatrixStack matrices) {
        final int contentSize = allButtons.size();
        if (contentSize == 0) {
            return;
        }
        
        scrollbarHeight = BUTTON_HEIGHT * VISIBLE_NUM;  // 全体の高さ
        scrollbarTop = getListY();  // スクロールバーの起点位置
        
        final double scrollbarRatio =
                VISIBLE_NUM < contentSize ? (1 - (contentSize - VISIBLE_NUM) / (double) contentSize) : 1;
        // スクロールバーの高さを計算
        scrollbarBarHeight = MathHelper.clamp((int) (scrollbarHeight * scrollbarRatio), 3, scrollbarHeight);
        
        final int barY =
                scrollbarTop + scrollOffset * (scrollbarHeight - scrollbarBarHeight) / (contentSize - VISIBLE_NUM);
        final int scrollbarBottom =
                scrollbarTop + contentSize * (scrollbarHeight - scrollbarBarHeight) / (contentSize - VISIBLE_NUM);
        
        final int x1 = getListX() + BUTTON_WIDTH + 10;  // スクロールバーのx座標
        final int x2 = getListX() + BUTTON_WIDTH + 15;  // スクロールバーのx座標（右端）
        
        // 描画
        fill(matrices, x1, scrollbarTop, x2, scrollbarBottom, 0xFF777777);
        fill(matrices, x1, barY, x2, barY + scrollbarBarHeight + 1, 0xFFAAAAAA);
    }
    
    private boolean isMouseOverScrollbar(double mouseX, double mouseY) {
        return mouseX >= getListX() + BUTTON_WIDTH + 10 && mouseX <= getListX() + BUTTON_WIDTH + 15 &&
                mouseY >= scrollbarTop && mouseY <= scrollbarTop + scrollbarHeight;
    }
}
