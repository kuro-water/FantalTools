package org.kgcc.fantalmod.registry;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.kgcc.fantalmod.skill.PickaxeSkill.AreaBreakSkill;
import org.kgcc.fantalmod.skill.PickaxeSkill.SmeltSkill;

public class AreaBreakEventHandler {
    /**
     * <p>ブロック破壊時に範囲破壊スキルが有効なプレイヤーの場合、
     * 範囲破壊を行うイベントハンドラ</p>
     * <p>{@link SmeltSkill#isActive(PlayerEntity)}を参照する</p>
     */
    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, blockState, blockEntity) -> {
            if (!AreaBreakSkill.isActive(player)) {
                return true;
            }
            if (world.isClient()) {
                return true;
            }
            if (!(world instanceof ServerWorld serverWorld)) {
                return true;
            }
            
            ItemStack mainHand = player.getMainHandStack();
            // ピッケルのみ有効
//            if (!(mainHand.getItem() instanceof PickaxeItem)) return true;
            // ツールの耐久値チェック
            
            // 石系ブロックのみ（例: 石、鉄鉱石など）に限定したい場合はここで判定
            if (!blockState.isIn(BlockTags.PICKAXE_MINEABLE))
                return true;
            
            // 3x3x3範囲を破壊
            BlockPos center = pos;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos target = center.add(dx, dy, dz);
                        if (target.equals(center))
                            continue; // 中心はバニラ処理に任せる
                        BlockState targetState = serverWorld.getBlockState(target);
                        if (targetState.isAir())
                            continue;
                        if (!targetState.isIn(BlockTags.PICKAXE_MINEABLE))
                            continue;
                        // ツールで破壊可能か
                        if (!mainHand.isSuitableFor(targetState))
                            continue;
                        // ドロップ
                        Block.getDroppedStacks(targetState, serverWorld, target, serverWorld.getBlockEntity(target))
                             .forEach(stack -> Block.dropStack(serverWorld, target, stack));
                        serverWorld.setBlockState(target, Blocks.AIR.getDefaultState());
                        // 破壊した分ツールの耐久値を減らす
                        if (!player.isCreative()) {
                            mainHand.damage(1, player, p -> p.sendToolBreakStatus(player.getActiveHand()));
                        }
                    }
                    
                }
            }
            return true; // 中心ブロックはバニラ処理
        });
    }
}