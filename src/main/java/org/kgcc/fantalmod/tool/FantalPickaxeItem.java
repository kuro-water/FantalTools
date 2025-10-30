package org.kgcc.fantalmod.tool;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.PickaxeItem;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.kgcc.fantalmod.registry.FantalModSkills;
import org.kgcc.fantalmod.skill.BaseSkill;

public class FantalPickaxeItem extends PickaxeItem implements FantalToolItem {
    // ...existing code...
    
    public FantalPickaxeItem() {
        super(new FantalToolMaterial(), 1, -2.8f, new Settings().rarity(Rarity.COMMON));
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // ...existing code...
        var result = super.use(world, user, hand);
        if (result.getResult() == ActionResult.SUCCESS) {
            return result;
        }
        ItemStack itemStack = user.getStackInHand(hand);
        BaseSkill skill = getSkill(itemStack);
        return skill.use(world, user, hand);
    }
    
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        // ...existing code...
        var result = super.useOnBlock(context);
        if (result == ActionResult.SUCCESS) {
            return result;
        }
        PlayerEntity player = context.getPlayer();
        if(player == null) {
            return result;
        }
        Hand hand = context.getHand();
        ItemStack itemStack = player.getStackInHand(hand);
        BaseSkill skill = getSkill(itemStack);
        return skill.useOnBlock(context);
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        BaseSkill skill = getSkill(stack);
        skill.inventoryTick(stack, world, entity, slot, selected);
    }
    
    @Override
    public void appendTooltip(ItemStack stack, World world, java.util.List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        BaseSkill skill = getSkill(stack);
        skill.appendTooltip(stack, world, tooltip, context);
    }
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {

    }
}
