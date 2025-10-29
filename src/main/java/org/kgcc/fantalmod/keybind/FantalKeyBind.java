package org.kgcc.fantalmod.keybind;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.tool.FantalToolItem;
import org.lwjgl.glfw.GLFW;

public class FantalKeyBind {
    public static KeyBinding pullFantalKeyBind;
    
    public static void registerKeyBindings() {
        pullFantalKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fantalmod.pull_Fantal",  // 翻訳キー
                InputUtil.Type.MOUSE,      // マウスの入力タイプ
                GLFW.GLFW_MOUSE_BUTTON_RIGHT, // 右クリックに設定
                "category.fantalmod"       // カテゴリ
        ));
        
        // キーバインドの動作を定義
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (pullFantalKeyBind.isPressed() && client.player != null) {
                client.player.sendMessage(Text.literal("[DEBUG] キーバインド実行！"), false);
                ItemStack mainHandItem = client.player.getMainHandStack();
                String skillName = FantalToolItem.readNbt(mainHandItem);
                FantalMod.LOGGER.info(skillName);
                if (mainHandItem.getItem() instanceof FantalToolItem fantalToolItem) {
                    FantalMod.LOGGER.info(fantalToolItem.getSkill().getName().getString());
                }
            }
        });
    }
}
