package org.kgcc.fantalmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;

public class FantalModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(TestBlock.CRYSTAL_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(TestBlock.CLEAR_BLOCK, RenderLayer.getCutout());
        
        ClientPlayNetworking.registerGlobalReceiver(FantalMod.FANTAL_POLLUTION,
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
                                                        FantalMod.LOGGER.info("{}の侵食度：{}", name,
                                                                              playerSpecificDirtBlocksBroken);
                                                    });
    }
}
