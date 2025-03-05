package org.kgcc.fantalmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
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
        
        ScreenEvents.AFTER_INIT.register(((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof GameMenuScreen))
                return;
            
            List<ClickableWidget> widgets = net.fabricmc.fabric.api.client.screen.v1.Screens.getButtons(screen);
            widgets.forEach(widget -> {
                FantalMod.LOGGER.info("{}", widget.getClass().getSimpleName());
            });
            
            ButtonWidget fantalmodsettingBtn = ButtonWidget.builder(Text.of("Fantal Mod"), (widget) -> {
                if (client.player != null)
                    client.player.sendMessage(Text.of("Fantal Mod楽しい！！！"));
            }).dimensions(screen.width / 2 - 102, screen.height / 4 + 128, 204, 20).build();
            widgets.add(fantalmodsettingBtn);
        }));
    }
}
