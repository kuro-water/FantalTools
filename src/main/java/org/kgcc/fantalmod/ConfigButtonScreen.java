package org.kgcc.fantalmod;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ConfigButtonScreen extends Screen {
    private final Screen parent; //Ecsメニュー画面を保存
    
    private ButtonWidget toggleImageButton; // 画面表示/非表示のボタン
    private ButtonWidget positionButton; // 位置切り替えボタン
    private SliderWidget customXSlider; // X軸スライダー
    private SliderWidget customYSlider; // Y軸スライダー
    private final List<ClickableWidget> customSliders = new ArrayList<>();
    
    protected ConfigButtonScreen(Screen parent) {
        super(Text.of("Mod 設定"));
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        // 画像の表示/非表示切り替えボタン
        toggleImageButton = ButtonWidget.builder(
                                                Text.of(FantalModState.isShowImage() ? "画像を非表示" : "画像を表示"),
                                                button -> {
                                                    boolean current = FantalModState.isShowImage();
                                                    FantalModState.setShowImage(!current);
                                                    button.setMessage(Text.of(!current ? "画像を非表示" : "画像を表示"));
                                                    rebuildUI();
                                                })
                                        .dimensions(centerX - 50, centerY - 60, 100, 20)
                                        .build();
        this.addDrawableChild(toggleImageButton);
        
        // 位置切り替えボタン（トグル式）
        positionButton = ButtonWidget.builder(
                                             Text.of("位置: " + getPositionLabel(FantalModState.getImagePosition())),
                                             button -> {
                                                 FantalModState.ImagePosition[] positions = FantalModState.ImagePosition.values();
                                                 int nextIndex = (FantalModState.getImagePosition().ordinal() + 1) % positions.length;
                                                 FantalModState.setImagePosition(positions[nextIndex]);
                                                 button.setMessage(Text.of("位置: " + getPositionLabel(positions[nextIndex])));
                                                 rebuildUI();
                                             })
                                     .dimensions(centerX - 50, centerY - 30, 100, 20)
                                     .build();
        if (FantalModState.isShowImage()) {
            this.addDrawableChild(positionButton);
        }
        
        // カスタム位置用スライダー
        if (FantalModState.getImagePosition() == FantalModState.ImagePosition.CUSTOM && FantalModState.isShowImage()) {
            int imageWidth = 64;
            int imageHeight = 64;
            int sliderMaxX = this.width - imageWidth;
            int sliderMaxY = this.height - imageHeight - 60;
            
            customXSlider = new SliderWidget(centerX - 70, centerY + 10, 140, 20,
                                             Text.of("X: " + FantalModState.getCustomX()),
                                             FantalModState.getCustomX() / (float) sliderMaxX) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Text.of("X: " + FantalModState.getCustomX()));
                }
                
                @Override
                protected void applyValue() {
                    int value = (int) (this.value * sliderMaxX); // 0~150のX軸スライダー
                    FantalModState.setCustomX(value);
                    this.setMessage(Text.of("X: " + value));
                }
            };
            customYSlider = new SliderWidget(centerX - 70, centerY + 35, 140, 20,
                                             Text.of("Y: " + FantalModState.getCustomY()),
                                             FantalModState.getCustomY() / (float) sliderMaxY) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Text.of("Y: " + FantalModState.getCustomY()));
                }
                
                @Override
                protected void applyValue() {
                    int value = (int) (this.value * sliderMaxY); // 0~150のY軸スライダー
                    FantalModState.setCustomY(value);
                    this.setMessage(Text.of("Y: " + value));
                }
            };
            
            this.customSliders.add(customXSlider);
            this.customSliders.add(customYSlider);
            this.addDrawableChild(customXSlider);
            this.addDrawableChild(customYSlider);
        }
        
        // 完了ボタン
        this.addDrawableChild(ButtonWidget.builder(Text.of("完了"), button -> {
            this.client.setScreen(parent);
        }).dimensions(centerX - 50, centerY + 70, 100, 20).build());
    }
    
    //ボタンの日本語表示
    private String getPositionLabel(FantalModState.ImagePosition position) {
        return switch (position) {
            case TOP_LEFT -> "左上";
            case TOP_RIGHT -> "右上";
            case BOTTOM_LEFT -> "左下";
            case BOTTOM_RIGHT -> "右下";
            case CUSTOM -> "カスタム";
        };
    }
    
    //UIの更新
    private void rebuildUI() {
        this.clearChildren();
        this.init();
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
    
    //設定画面の背景を半透明の薄暗いやつに
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        
        super.render(matrices, mouseX, mouseY, delta);
    }
}