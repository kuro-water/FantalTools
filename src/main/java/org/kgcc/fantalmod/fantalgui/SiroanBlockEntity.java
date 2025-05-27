package org.kgcc.fantalmod.fantalgui;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.gui.ImplementedInventory;
import org.kgcc.fantalmod.registry.FantalBlockEntities;


public class SiroanBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 72;
    
    public SiroanBlockEntity(BlockPos pos, BlockState state) {
        super(FantalBlockEntities.SIROAN_BLOCK, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            public int get(int index) {
                switch (index) {
                    case 0:
                        return org.kgcc.fantalmod.fantalgui.SiroanBlockEntity.this.progress;
                    case 1:
                        return org.kgcc.fantalmod.fantalgui.SiroanBlockEntity.this.maxProgress;
                    default:
                        return 0;
                }
            }
            
            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        org.kgcc.fantalmod.fantalgui.SiroanBlockEntity.this.progress = value;
                        break;
                    case 1:
                        org.kgcc.fantalmod.fantalgui.SiroanBlockEntity.this.maxProgress = value;
                        break;
                }
            }
            
            public int size() {
                return 2;
            }
        };
    }
    
    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }
    
    @Override
    public Text getDisplayName() {
        return Text.literal("Gem Infusing Station");
    }
    
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new SiroanBlockScreenHandler(syncId, inv, this, this.propertyDelegate);
    }
    
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("siroan_block.progress", progress);
    }
    
    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, inventory);
        super.readNbt(nbt);
        progress = nbt.getInt("siroan_block.progress");
    }
    
    public void sync() {
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }
}