package org.kgcc.fantalmod;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class FantalSliderWidget extends SliderWidget {
    private final int min;
    private final int max;
    private final ValueChangeCallback callback;
    
    public interface ValueChangeCallback {
        void onValueChanged(int value);
    }
    
    public FantalSliderWidget(int x, int y, int width, int height, int min, int max, int currentValue, ValueChangeCallback callback) {
        super(x, y, width, height, Text.of(""), (currentValue - min) / (double) (max - min));
        this.min = min;
        this.max = max;
        this.callback = callback;
        this.updateMessage();
    }
    
    @Override
    protected void updateMessage() {
        int value = (int) (this.value * (max - min) + min);
        this.setMessage(Text.of("値: " + value));
    }
    
    @Override
    protected void applyValue() {
        int value = (int) (this.value * (max - min) + min);
        callback.onValueChanged(value);
    }
}
