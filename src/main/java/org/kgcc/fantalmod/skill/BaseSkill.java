package org.kgcc.fantalmod.skill;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public interface BaseSkill {
    // todo: 汚染度をどこで変更するか
    
    String getName();
    
    default TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
    
    default ActionResult useOnBlock(ItemUsageContext context) {
        return ActionResult.PASS;
    }
    
    default void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
    }
    
    default void appendTooltip(ItemStack stack, World world, java.util.List<Text> tooltip, TooltipContext context) {
//        tooltip.add(Text.literal(skill.getName()).formatted(Formatting.AQUA));
//        tooltip.add(Text.literal(skill.getName()).formatted(Formatting.DARK_AQUA));
//        tooltip.add(Text.literal(skill.getName()).formatted(Formatting.YELLOW));
//        tooltip.add(Text.literal(skill.getName()).formatted(Formatting.WHITE));
        tooltip.add(Text.literal(getName()).formatted(Formatting.AQUA, Formatting.BOLD));
//        tooltip.add(Text.literal(skill.getName()).formatted(Formatting.DARK_AQUA, Formatting.BOLD));
//        tooltip.add(Text.literal(skill.getName()).formatted(Formatting.YELLOW, Formatting.BOLD));
//        tooltip.add(Text.literal(skill.getName()).formatted(Formatting.WHITE, Formatting.BOLD));
    }
}
