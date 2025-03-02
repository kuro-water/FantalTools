package org.kgcc.fantalmod.tool;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.kgcc.fantalmod.util.FantalStateManager;

public class FantalAxeItem extends AxeItem {
    public FantalAxeItem() {
        super(new FantalToolMaterial(), 5f, -3f, new Settings().rarity(Rarity.COMMON));
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.getWorld().isClient() && hand == Hand.MAIN_HAND) {
            var server = world.getServer();
            user.addStatusEffect(
                    new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 20 * FantalStateManager.TICK_PAR_SEC, 1));
            FantalStateManager.addFantalPollution(server, user, 1);
            FantalStateManager.sendFantalPollution(server, user);
        }
        return super.use(world, user, hand);
    }
}
