package org.kgcc.fantalmod.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.material.FantalToolMaterial;
import org.kgcc.fantalmod.util.FantalStateManager;

import static org.kgcc.fantalmod.FantalMod.MODID;

public class FantalSwordItem extends SwordItem {
    public FantalSwordItem() {
        super(new FantalToolMaterial(), 3, -2.4f, new Item.Settings().rarity(Rarity.COMMON));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.getWorld().isClient() && hand == Hand.MAIN_HAND) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * FantalMod.TICK_PAR_SEC, 1));
            FantalStateManager.incrementFantalPollution(world, user);
        }
        return super.use(world, user, hand);
    }

    public static Item register(Item instance, String path) {
        return Registry.register(Registries.ITEM, new Identifier(MODID, path), instance);
    }
}
