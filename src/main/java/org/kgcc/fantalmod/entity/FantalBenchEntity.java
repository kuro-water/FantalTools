package org.kgcc.fantalmod.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.kgcc.fantalmod.block.FantalBench;
import org.kgcc.fantalmod.registry.FantalBlockEntities;
import org.kgcc.fantalmod.screen.FantalBenchScreenHandler;
import org.kgcc.fantalmod.screen.ImplementedInventory;


public class FantalBenchEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    
    public FantalBenchEntity(BlockPos pos, BlockState state) {
        super(FantalBlockEntities.FANTAL_BENCH, pos, state);
    }
    
    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }
    
    @Override
    public Text getDisplayName() {
        return Text.translatable("block.fantalmod.fantal_bench");
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
    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null && !world.isClient) {
            updateAppearanceByTool();
        }
    }

    private void updateAppearanceByTool() {
        ItemStack tool = inventory.get(0); // 0番スロットをツール用に想定
        int appearance = 0;
        if (tool.getItem() instanceof PickaxeItem) {
            appearance = 1;
        } else if (tool.getItem() instanceof AxeItem) {
            appearance = 2;
        } else if(tool.getItem() instanceof SwordItem) {
            appearance = 3;
        }

        BlockState state = world.getBlockState(pos);
        if (state.get(FantalBench.APPEARANCE) != appearance) {
            world.setBlockState(pos, state.with(FantalBench.APPEARANCE, appearance), 3);
        }
    }
}