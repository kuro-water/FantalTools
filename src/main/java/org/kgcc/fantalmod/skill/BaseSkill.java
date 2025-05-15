package org.kgcc.fantalmod.skill;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public interface BaseSkill {
    // todo: 汚染度をどこで変更するか
    
    default TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
    
    default ActionResult useOnBlock(ItemUsageContext context) {
        return ActionResult.PASS;
    }
}
