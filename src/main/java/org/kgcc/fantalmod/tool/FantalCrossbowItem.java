package org.kgcc.fantalmod.tool;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class FantalCrossbowItem extends CrossbowItem {

    public FantalCrossbowItem() {
        super(new Item.Settings().maxCount(1).rarity(Rarity.COMMON));
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000; // クロスボウのチャージ時間
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.CROSSBOW;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (isCharged(itemStack)) {
            // すでにチャージ済みなら発射する
            shootAll(world, user, hand, itemStack, getSpeed(itemStack), 1.0F);
            setCharged(itemStack, false);
            return TypedActionResult.success(itemStack, world.isClient());
        } else {
            // チャージを始める
            return ItemUsage.consumeHeldItem(world, user, hand);
        }
    }

    private float getSpeed(ItemStack stack) {
        return 3.15F; // 通常クロスボウと同じ速度
    }
}
