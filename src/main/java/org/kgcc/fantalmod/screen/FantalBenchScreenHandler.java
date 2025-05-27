package org.kgcc.fantalmod.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.kgcc.fantalmod.entity.FantalBenchEntity;
import org.kgcc.fantalmod.registry.FantalModItems;
import org.kgcc.fantalmod.registry.ModScreenHandlers;

public class FantalBenchScreenHandler extends ScreenHandler {
    public final Inventory inventory;
    
    public FantalBenchScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, new SimpleInventory(3));
    }
    
    public FantalBenchScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ModScreenHandlers.SIROAN_BLOCK_SCREEN_HANDLER, syncId);
        checkSize(inventory, 3);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
        //スロットの位置
        this.addSlot(new Slot(inventory, 0, 12, 15));//入力
        
        this.addSlot(new Slot(inventory, 1, 12, 60));//出力
        
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }
    
    //プレイヤーのインベントリスロット
    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 86 + i * 18));
            }
        }
    }
    
    //プレイヤーホットバースロット
    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 144));
        }
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack newStack = stack.copy();
            
            // 特定のアイテムのみスロットに移動可能
            if (stack.getItem() == FantalModItems.RED_SMALL) {
                if (!this.insertItem(stack, 1, 2, false)) { // スロット1に移動
                    return ItemStack.EMPTY;
                }
            } else if (stack.getItem() == FantalModItems.FANTAL_SWORD
                    || stack.getItem() == FantalModItems.FANTAL_AXE
                    || stack.getItem() == FantalModItems.FANTAL_PICKAXE
                    || stack.getItem() == FantalModItems.FANTAL_SHOVEL
                    || stack.getItem() == FantalModItems.FANTAL_HOE) {
                if (!this.insertItem(stack, 0, 1, false)) { // スロット0に移動
                    return ItemStack.EMPTY;
                }
            } else {
                // 特定のアイテム以外はスロットに移動不可
                return ItemStack.EMPTY;
            }
            
            if (stack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
            
            return newStack;
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id == 0) {
            // 剣の効果を有効にするボタンが押されたときの処理
            if (this.inventory instanceof FantalBenchEntity fantalBenchEntity) {
                fantalBenchEntity.getStack(1).decrement(1);
                fantalBenchEntity.markDirty();
                var nbt = new NbtCompound();
                fantalBenchEntity.writeNbt(nbt);
            }
        }
        return true;
    }
    
}