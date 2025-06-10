package org.kgcc.fantalmod.skill;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.kgcc.fantalmod.util.FantalStateManager;

public class SpeedSkill implements BaseSkill {
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        
        user.addStatusEffect(
                new StatusEffectInstance(
                        StatusEffects.SPEED,
                        20 * FantalStateManager.TICK_PAR_SEC,
                        1));
        
        var server = world.getServer();
        FantalStateManager.addFantalPollution(server, user, 1);
        FantalStateManager.sendFantalPollution(server, user);
        
        return TypedActionResult.success(user.getStackInHand(hand));
    }
    
    @Override
    public String getName() {
        return "speed";
    }
}
