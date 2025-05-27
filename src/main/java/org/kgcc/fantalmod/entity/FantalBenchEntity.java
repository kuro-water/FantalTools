package org.kgcc.fantalmod.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.kgcc.fantalmod.screen.FantalBenchScreenHandler;
import org.kgcc.fantalmod.screen.ImplementedInventory;
import org.kgcc.fantalmod.registry.FantalBlockEntities;


public class FantalBenchEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    
    public FantalBenchEntity(BlockPos pos, BlockState state) {
        super(FantalBlockEntities.SIROAN_BLOCK, pos, state);
    }
    
    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }
    
    @Override
    public Text getDisplayName() {
        // todo: Localize this string
        return Text.literal("Siroan Block");
    }
    
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new FantalBenchScreenHandler(syncId, inv, this);
    }
    
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
    }
    
    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, inventory);
        super.readNbt(nbt);
    }
    
}