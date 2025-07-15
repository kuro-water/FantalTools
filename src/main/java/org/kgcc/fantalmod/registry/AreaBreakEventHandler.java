package org.kgcc.fantalmod.registry;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.kgcc.fantalmod.skill.PickaxeSkill.AreaBreakSkill;

public class AreaBreakEventHandler {
    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient()) return true;
            if (!(world instanceof ServerWorld serverWorld)) return true;
            if (!AreaBreakSkill.isActive(player)) return true;

            ItemStack mainHand = player.getMainHandStack();
            // ピッケルのみ有効
            if (!(mainHand.getItem() instanceof PickaxeItem)) return true;
            // ツールの耐久値チェック

            // 石系ブロックのみ（例: 石、鉄鉱石など）に限定したい場合はここで判定
            if (!state.isIn(BlockTags.PICKAXE_MINEABLE)) return true;

            // 3x3範囲を破壊
            BlockPos center = pos;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos target = center.add(dx, dy, dz);
                        mainHand.damage(1, player, p -> p.sendToolBreakStatus(player.getActiveHand()));
                    }
                }

            }
            return true; // 中心ブロックはバニラ処理
        });
    }
}