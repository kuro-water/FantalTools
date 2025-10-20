package org.kgcc.fantalmod.util;

import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import org.kgcc.fantalmod.registry.FantalModItems;
import net.minecraft.util.Identifier;
import net.minecraft.item.ItemStack;

public class ModModelPredicateProvider {
    public static void registerModModels() {
        registerBow();
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
