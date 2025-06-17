package org.kgcc.fantalmod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CustomFontButtonWidget extends ButtonWidget {
    private static final Identifier TEXTURE = new Identifier("minecraft", "textures/gui/widgets.png");
    private static final Identifier VANILLA_BUTTON_TEXTURE = new Identifier("fantalmod", "textures/gui/vanilla_button.png");
    private static final Identifier VANILLA_BUTTON_SELECTED_TEXTURE = new Identifier("fantalmod", "textures/gui/vanilla_button_selected.png");
    private final float scale;

    public CustomFontButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, float scale, Tooltip tooltip) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.scale = scale;
        this.setTooltip(tooltip);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!this.visible) return;

        MinecraftClient client = MinecraftClient.getInstance();
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = this.getX();
        int y = this.getY();
        int width = this.getWidth();
        int height = this.getHeight();

//        int halfWidth = width / 2;
//        int halfHeight = height / 2;

        int v=0;
        if (!this.active) {
            v = 46 + 0 * 20; // 無効状態
        } else if (this.isHovered()) {
//            v = 46 + 2 * 20; // ホバー状態
            RenderSystem.setShaderTexture(0, VANILLA_BUTTON_SELECTED_TEXTURE);
//            DrawableHelper.drawTexture(matrices, x, y, 0, v, halfWidth, halfHeight, 256, 256);
//            DrawableHelper.drawTexture(matrices, x + halfWidth, y, 200 - halfWidth, v, halfWidth, halfHeight, 256, 256);
//            DrawableHelper.drawTexture(matrices, x, y + halfHeight, 0, v +20- halfHeight, halfWidth, halfHeight, 256, 256);
//            DrawableHelper.drawTexture(matrices, x + halfWidth, y + halfHeight, 200 - halfWidth, v +20- halfHeight, halfWidth, halfHeight, 256, 256);
        } else {
//            v = 46 + 1 * 20; // 通常状態
            RenderSystem.setShaderTexture(0, VANILLA_BUTTON_TEXTURE);
//            DrawableHelper.drawTexture(matrices, x, y, 0, v, halfWidth, halfHeight, 256, 256);
//            DrawableHelper.drawTexture(matrices, x + halfWidth, y, 200 - halfWidth, v, halfWidth, halfHeight, 256, 256);
//            DrawableHelper.drawTexture(matrices, x, y + halfHeight, 0, v +20- halfHeight, halfWidth, halfHeight, 256, 256);
//            DrawableHelper.drawTexture(matrices, x + halfWidth, y + halfHeight, 200 - halfWidth, v +19- halfHeight, halfWidth, halfHeight, 256, 256);
        }

        // ShaderColorはMinecraftデフォルトの色（白）に固定
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

//        int halfWidth = width / 2;
//        int halfHeight = height / 2;
        DrawableHelper.drawTexture(matrices, x, y, 0, v, width, height, width, height);
//        DrawableHelper.drawTexture(matrices, x + halfWidth, y, 200 - halfWidth, v, width - halfWidth, height, 256, 256);

        // テキストの描画
        int textColor = 0xE0E0E0; // Minecraftデフォルトのテキスト色
        matrices.push();
        float textWidth = client.textRenderer.getWidth(this.getMessage()) * scale;
        float textHeight = 8 * scale;
        float textX = x + (width - textWidth) / 2;
        float textY = y + (height - textHeight) / 2;
        matrices.translate(textX, textY, 0);
        matrices.scale(scale, scale, 1.0f);
        client.textRenderer.drawWithShadow(matrices, this.getMessage(), 0, 0, textColor);
        matrices.pop();
    }
}