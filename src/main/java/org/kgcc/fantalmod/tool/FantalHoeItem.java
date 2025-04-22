package org.kgcc.fantalmod.tool;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.test.Recall;

import java.util.Objects;

public class FantalHoeItem extends HoeItem {
    private final Recall playerState = new Recall();
    
    public FantalHoeItem() {
        super(new FantalToolMaterial(), -3, 0, new Settings().rarity(Rarity.COMMON));
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.getWorld().isClient() && hand == Hand.MAIN_HAND) {
            var server = Objects.requireNonNull(world.getServer());
//            user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20 * FantalStateManager.TICK_PAR_SEC, 0));
//            FantalStateManager.addFantalPollution(server, user,1);
//            FantalStateManager.sendFantalPollution(server, user);
            playerState.recall(server, user);
        }
        return super.use(world, user, hand);
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!world.isClient) {
            playerState.record((PlayerEntity) entity);
        }
    }
}
