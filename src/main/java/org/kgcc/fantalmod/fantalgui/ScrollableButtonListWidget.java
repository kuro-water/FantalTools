package org.kgcc.fantalmod.fantalgui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ScrollableButtonListWidget extends EntryListWidget<ScrollableButtonListWidget.ButtonEntry> {
    public ScrollableButtonListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
    }

    public void addButton(ButtonWidget button) {
        this.addEntry(new ButtonEntry(button));
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    public static class ButtonEntry extends EntryListWidget.Entry<ButtonEntry> {
        private final ButtonWidget button;

        public ButtonEntry(ButtonWidget button) {
            this.button = button;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
            button.setPosition(x, y);
            button.render(matrices, mouseX, mouseY, delta);
        }
    }
}