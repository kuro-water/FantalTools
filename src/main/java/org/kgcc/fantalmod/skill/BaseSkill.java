package org.kgcc.fantalmod.skill;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.kgcc.fantalmod.FantalMod;

public interface BaseSkill {
    // todo: 汚染度をどこで変更するか
    
    String getTranslationKey();
    
    default MutableText getName() {
        return Text.translatable("skill.fantalmod." + getTranslationKey());
    }
    
    default MutableText getTooltip(){
        return Text.translatable("skill.fantalmod." + getTranslationKey() + ".description");
    }
    
    default TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
    
    default ActionResult useOnBlock(ItemUsageContext context) {
        return ActionResult.PASS;
    }
    
    default void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
    }
    
    default void appendTooltip(ItemStack stack, World world, java.util.List<Text> tooltip, TooltipContext context) {
        tooltip.add(getName().formatted(Formatting.AQUA));
    }

}
