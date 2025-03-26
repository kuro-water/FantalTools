package org.kgcc.fantalmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.kgcc.fantalmod.keybind.FantalKeyBind;

public class FantalModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // キーバインドの登録
        FantalKeyBind.registerKeyBindings();

        // 侵食度のネットワークメッセージ受信処理
        ClientPlayNetworking.registerGlobalReceiver(FantalMod.FANTAL_POLLUTION,
                (client, handler, buf, responseSender) -> {
                    int totalFantalPollution = buf.readInt();
                    int playerSpecificDirtBlocksBroken = buf.readInt();
                    MinecraftClient mc = client;
                    if (mc.player == null) {
                        return;
                    }
                    String name = mc.player.getDisplayName().getString();

                    mc.execute(() -> {
                        if (mc.player != null) {
                            mc.player.sendMessage(Text.literal(name + "の侵食度：" + playerSpecificDirtBlocksBroken));
                        }
                    });

                    FantalMod.LOGGER.info("全体の侵食度：{}", totalFantalPollution);
                    FantalMod.LOGGER.info("{}の侵食度：{}", name, playerSpecificDirtBlocksBroken);
                }
        );
    }
}
