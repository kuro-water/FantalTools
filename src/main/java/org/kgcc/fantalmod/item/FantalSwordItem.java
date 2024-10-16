package org.kgcc.fantalmod.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.material.FantalToolMaterial;
import org.kgcc.fantalmod.util.FantalStateManager;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.ItemGroup;




import static org.kgcc.fantalmod.FantalMod.MODID;
import static org.kgcc.fantalmod.registry.ModItems.FANTAL_INGOT;

public class FantalSwordItem extends SwordItem {
    private static final int DURABILITY = 1561; // 耐久値
    private static final float MINING_SPEED_MULTIPLIER = 8.0F; // 採掘速度
    private static final int MINING_LEVEL = 3; // 採掘レベル

    public FantalSwordItem() {
        super(new FantalToolMaterial(), 1, 1.0f, new Item.Settings().rarity(Rarity.COMMON));
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
