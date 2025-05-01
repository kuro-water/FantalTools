package org.kgcc.fantalmod.util;

import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import org.kgcc.fantalmod.registry.FantalModItems;
import net.minecraft.util.Identifier;
import net.minecraft.item.ItemStack;

public class ModModelPredicateProvider {
    public static void registerModModels() {
        registerBow();
        registerCrossbow();
        registerTue();
    }

    private static void registerBow() {
        FabricModelPredicateProviderRegistry.register(FantalModItems.FANTAL_BOW, new Identifier("pull"),
                (stack, world, entity, seed) -> {
                    if (entity == null) {
                        return 0.0f;
                    }
                    if (entity.getActiveItem() != stack) {
                        return 0.0f;
                    }
                    return (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0f;
                });

        FabricModelPredicateProviderRegistry.register(FantalModItems.FANTAL_BOW, new Identifier("pulling"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem()
                        && entity.getActiveItem() == stack ? 1.0f : 0.0f);
    }

    private static void registerCrossbow() {
        // pulling
        FabricModelPredicateProviderRegistry.register(FantalModItems.FANTAL_CROSSBOW, new Identifier("pulling"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem()
                        && entity.getActiveItem() == stack ? 1.0f : 0.0f);

        // pull
        FabricModelPredicateProviderRegistry.register(FantalModItems.FANTAL_CROSSBOW, new Identifier("pull"),
                (stack, world, entity, seed) -> {
                    if (entity == null) return 0.0f;
                    return entity.getActiveItem() != stack ? 0.0f :
                            (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0f;
                });

        // charged
        FabricModelPredicateProviderRegistry.register(FantalModItems.FANTAL_CROSSBOW, new Identifier("charged"),
                (stack, world, entity, seed) -> entity != null && entity.getActiveItem() == stack ? 1.0f : 0.0f);

        // fireworkを別の状態で管理
        FabricModelPredicateProviderRegistry.register(FantalModItems.FANTAL_CROSSBOW, new Identifier("firework"),
                (stack, world, entity, seed) -> {
                    if (stack.hasCustomName() && stack.getName().getString().equals("Firework")) {  // アイテム名で管理する例
                        return 1.0f;  // Firework状態
                    }
                    return 0.0f;  // 通常状態
                });

        // 横向きに変更するためのアニメーション設定
        FabricModelPredicateProviderRegistry.register(FantalModItems.FANTAL_CROSSBOW, new Identifier("rotation"),
                (stack, world, entity, seed) -> 90.0f);  // 横向きに回転させる
    }

    private static void registerTue() {
        FabricModelPredicateProviderRegistry.register(FantalModItems.FANTAL_BOW, new Identifier("pull"),
                (stack, world, entity, seed) -> {
                    if (entity == null) {
                        return 0.0f;
                    }
                    if (entity.getActiveItem() != stack) {
                        return 0.0f;
                    }
                    return (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0f;
                });

        FabricModelPredicateProviderRegistry.register(FantalModItems.FANTAL_BOW, new Identifier("pulling"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem()
                        && entity.getActiveItem() == stack ? 1.0f : 0.0f);
    }
}
