package org.kgcc.fantalmod.keybind;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.kgcc.fantalmod.tool.FantalBowItem;

public class InputEvents {

    public static void registerKeyInput() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (FantalKeyBind.pullBowKey.isPressed()) {
                var player = client.player;
                if (player != null) {
                    ItemStack heldItem = player.getMainHandStack();
                    if (heldItem.getItem() instanceof FantalBowItem) {
                        System.out.println("[DEBUG] キーで弓を引きました！");
                        player.setCurrentHand(Hand.MAIN_HAND);
                    }
                }
            }
        });
    }
}
