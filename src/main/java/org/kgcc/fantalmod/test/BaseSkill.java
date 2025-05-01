package org.kgcc.fantalmod.test;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public interface BaseSkill {
    // todo: 汚染度をどこで変更するか
    
    /**
     * スキルを使用する
     * @return 蓄積する汚染度
     */
    TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand);
    
    ActionResult useOnBlock(ItemUsageContext context);
}
