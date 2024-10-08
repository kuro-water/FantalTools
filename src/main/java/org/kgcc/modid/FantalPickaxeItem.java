package org.kgcc.modid;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.*;
import net.minecraft.world.World;

import static org.kgcc.modid.FantalMod.MODID;

public class FantalPickaxeItem extends Item {
    public FantalPickaxeItem() {
        super(new Settings().rarity(Rarity.COMMON));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.getWorld().isClient() && hand == Hand.MAIN_HAND) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 20 * FantalMod.TICK_PAR_SEC, 1));
            FantalStateManager.incrementFantalPollution(world, user);
        }
        return super.use(world, user, hand);
    }

    public static Item register(Item instance, String path) {
        return Registry.register(Registries.ITEM, new Identifier(MODID, path), instance);
    }
}
