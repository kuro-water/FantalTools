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
import org.kgcc.fantalmod.registry.FantalModScreenHandlers;
import org.kgcc.fantalmod.screen.FantalBenchScreen;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.init.ModEntities;
import org.kgcc.fantalmod.keybind.FantalKeyBind;
import org.kgcc.fantalmod.registry.FantalModItems;
import org.kgcc.fantalmod.client.render.FantalArrowEntityRenderer;



import java.util.List;

@Environment(EnvType.CLIENT)
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
        // ModelPredicateの設定を一つの関数でまとめる
        registerModelPredicates();

        // キーバインドの登録
        FantalKeyBind.registerKeyBindings();

        // 侵食度のネットワークメッセージ受信処理
        ClientPlayNetworking.registerGlobalReceiver(FantalMod.FANTAL_POLLUTION,
                (client, handler, buf, responseSender) -> {
                    int totalFantalPollution = buf.readInt();
                    int playerSpecificDirtBlocksBroken = buf.readInt();
                    if (client.player == null) {
                        return;
                    }
                    String name = client.player.getDisplayName().getString();

                    client.execute(() -> {
                        if (client.player != null) {
                            client.player.sendMessage(Text.literal(name + "の侵食度：" + playerSpecificDirtBlocksBroken));
                        }
                    });

                    FantalMod.LOGGER.info("全体の侵食度：{}", totalFantalPollution);
                    FantalMod.LOGGER.info("{}の侵食度：{}", name, playerSpecificDirtBlocksBroken);
                }
        );

        EntityRendererRegistry.register(ModEntities.FANTAL_ARROW_ENTITY, FantalArrowEntityRenderer::new);

    }

    private void registerModelPredicates() {
        // 弓
        registerBowPredicates();

        // 杖
        registerWandPredicates();
    }

    private void registerBowPredicates() {
        ModelPredicateProviderRegistry.register(
                FantalModItems.FANTAL_BOW, new Identifier("pulling"),
                (stack, world, entity, seed) -> (entity != null && entity.isUsingItem() && entity.getActiveItem() == stack) ? 1.0F : 0.0F
        );

        ModelPredicateProviderRegistry.register(
                FantalModItems.FANTAL_BOW, new Identifier("pull"),
                (stack, world, entity, seed) -> {
                    if (entity == null) return 0.0F;
                    return entity.getActiveItem() != stack ? 0.0F :
                            (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
                }
        );
    }

    private void registerWandPredicates() {
        ModelPredicateProviderRegistry.register(
                FantalModItems.FANTAL_WAND, new Identifier("pulling"),
                (stack, world, entity, seed) -> (entity != null && entity.isUsingItem() && entity.getActiveItem() == stack) ? 1.0F : 0.0F
        );

        ModelPredicateProviderRegistry.register(
                FantalModItems.FANTAL_WAND, new Identifier("pull"),
                (stack, world, entity, seed) -> {
                    if (entity == null) return 0.0F;
                    return entity.getActiveItem() != stack ? 0.0F :
                            (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
                }
        );
    }

}
