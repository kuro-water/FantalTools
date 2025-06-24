// src/main/java/org/kgcc/fantalmod/registry/OreSmeltEventHandler.java
package org.kgcc.fantalmod.registry;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.kgcc.fantalmod.skill.SmeltSkill;

public class OreSmeltEventHandler {
    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient()) return true;
            if (!(world instanceof ServerWorld serverWorld)) return true;
            if (!SmeltSkill.isActive(player)) return true;

            ItemStack drop = ItemStack.EMPTY;
            if (state.getBlock() == Blocks.IRON_ORE) {
                drop = new ItemStack(Items.IRON_INGOT);
            } else if (state.getBlock() == Blocks.GOLD_ORE) {
                drop = new ItemStack(Items.GOLD_INGOT);
            } else if (state.getBlock() == Blocks.COPPER_ORE) {
                drop = new ItemStack(Items.COPPER_INGOT);
            }

            if (!drop.isEmpty()) {
                // ブロックを手動で壊す
                serverWorld.setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState());
                Vec3d vec = Vec3d.ofCenter(pos);
                serverWorld.spawnEntity(new ItemEntity(serverWorld, vec.x, vec.y, vec.z, drop));
                return false; // バニラのドロップと破壊をキャンセル
            }
            return true;
        });
    }
}