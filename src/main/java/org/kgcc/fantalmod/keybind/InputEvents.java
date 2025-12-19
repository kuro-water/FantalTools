package org.kgcc.fantalmod.keybind;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class InputEvents {

    public static void registerKeyInput() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (FantalKeyBind.pullFantalKeyBind.isPressed()) {
                var player = client.player;
                if (player != null) {
                    System.out.println("[DEBUG] キーバインド発動！");
                }
            }
        });
    }
}
