package org.kgcc.fantalmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.keybind.FantalKeyBind;
import org.kgcc.fantalmod.registry.FantalModItems;

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
    }

    private void registerModelPredicates() {
        // 弓
        registerBowPredicates(FantalModItems.FANTAL_BOW);

        // クロスボウ
        registerCrossbowPredicates(FantalModItems.FANTAL_CROSSBOW);

        // 盾
        registerShieldPredicates(FantalModItems.FANTAL_SHIELD);

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

    private void registerCrossbowPredicates(Item item) {
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

        ModelPredicateProviderRegistry.register(
                item, new Identifier("charged"),
                (stack, world, entity, seed) -> entity != null && entity.getActiveItem() == stack ? 1.0F : 0.0F
        );

        ModelPredicateProviderRegistry.register(
                item, new Identifier("firework"),
                (stack, world, entity, seed) -> {
                    if (entity != null && entity.isUsingItem()) {
                        return stack.getOrCreateNbt().getBoolean("Firework") ? 1.0F : 0.0F;
                    }
                    return 0.0F;
                }
        );
    }

    private void registerShieldPredicates(Item item) {
        ModelPredicateProviderRegistry.register(
                item, new Identifier("blocking"),
                (stack, world, entity, seed) -> (entity != null && entity.isUsingItem() && entity.getActiveItem() == stack) ? 1.0F : 0.0F
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
