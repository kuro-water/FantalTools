package org.kgcc.fantalmod.fantalgui;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.function.Predicate;

// カスタムスロットクラス
public class RestrictedSlot extends Slot {
    private final Predicate<ItemStack> validator;

    public RestrictedSlot(Inventory inventory, int index, int x, int y, Predicate<ItemStack> validator) {
        super(inventory, index, x, y);
        this.validator = validator;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return validator.test(stack);
    }
}