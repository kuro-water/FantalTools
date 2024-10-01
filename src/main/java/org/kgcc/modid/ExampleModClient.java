package org.kgcc.modid;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.text.Text;

public class ExampleModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ExampleMod.FANTAL_POLLUTION, (client, handler, buf, responseSender) -> {
            int totalFantalPollution = buf.readInt();
            ExampleMod.LOGGER.info("init");
            client.execute(() -> {
                client.player.sendMessage(Text.literal("現在の侵食度： " + totalFantalPollution));
            });
        });
    }
}
