package org.kgcc.fantalmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.entity.projectile.FantalSnowballEntity;
import org.kgcc.fantalmod.init.ModEntities;
import org.kgcc.fantalmod.keybind.FantalKeyBind;
import org.kgcc.fantalmod.registry.FantalModItems;
import org.kgcc.fantalmod.client.render.FantalArrowEntityRenderer;



public class FantalModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // ModelPredicateの設定を一つの関数でまとめる
        registerModelPredicates();

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

        EntityRendererRegistry.register(ModEntities.FANTAL_ARROW_ENTITY, FantalArrowEntityRenderer::new);

    }

    private void registerModelPredicates() {
        // 弓
        registerBowPredicates(FantalModItems.FANTAL_BOW);

        // 杖
        registerTuePredicates(FantalModItems.FANTAL_TUE);
    }

    private void registerBowPredicates(Item item) {
        ModelPredicateProviderRegistry.register(
                item, new Identifier("pulling"),
                (stack, world, entity, seed) -> (entity != null && entity.isUsingItem() && entity.getActiveItem() == stack) ? 1.0F : 0.0F
        );

        ModelPredicateProviderRegistry.register(
                item, new Identifier("pull"),
                (stack, world, entity, seed) -> {
                    if (entity == null) return 0.0F;
                    return entity.getActiveItem() != stack ? 0.0F :
                            (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
                }
        );
    }

    private void registerTuePredicates(Item item) {
        ModelPredicateProviderRegistry.register(
                item, new Identifier("pulling"),
                (stack, world, entity, seed) -> (entity != null && entity.isUsingItem() && entity.getActiveItem() == stack) ? 1.0F : 0.0F
        );

        ModelPredicateProviderRegistry.register(
                item, new Identifier("pull"),
                (stack, world, entity, seed) -> {
                    if (entity == null) return 0.0F;
                    return entity.getActiveItem() != stack ? 0.0F :
                            (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
                }
        );
    }

}
