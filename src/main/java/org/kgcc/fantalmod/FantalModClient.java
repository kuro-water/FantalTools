package org.kgcc.fantalmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.command.SkillArgumentType;
import org.kgcc.fantalmod.registry.FantalModScreenHandlers;
import org.kgcc.fantalmod.screen.FantalBenchScreen;

public class FantalModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(
                FantalMod.FANTAL_POLLUTION,
                (client, handler, buf, responseSender) -> {
                    var totalFantalPollution = buf.readInt();
                    var playerSpecificDirtBlocksBroken = buf.readInt();
                    var player = client.player;
                    if (player == null) {
                        return;
                    }
                    var name = player.getDisplayName().getString();
                    
                    client.execute(() -> {
                        if (client.player != null) {
//                                                                client.player.sendMessage(Text.literal(
//                                                                        "全体の侵食度：" + totalFantalPollution));
                            client.player.sendMessage(Text.literal(
                                    name + "の侵食度：" + playerSpecificDirtBlocksBroken));
                        }
                    });
                    
                    FantalMod.LOGGER.info("全体の侵食度：{}", totalFantalPollution);
                    FantalMod.LOGGER.info("{}の侵食度：{}", name, playerSpecificDirtBlocksBroken);
                });
        HandledScreens.register(FantalModScreenHandlers.FANTAL_BENCH_SCREEN_HANDLER, FantalBenchScreen::new);
    }
}
