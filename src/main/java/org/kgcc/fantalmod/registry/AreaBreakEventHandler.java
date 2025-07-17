package org.kgcc.fantalmod.registry;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.kgcc.fantalmod.skill.HammerSkill;
import org.kgcc.fantalmod.skill.SmeltSkill;
import org.kgcc.fantalmod.util.FantalStateManager;

public class AreaBreakEventHandler {
    /**
     * <p>ブロック破壊時に範囲破壊スキルが有効なプレイヤーの場合、
     * 範囲破壊を行うイベントハンドラ</p>
     * <p>{@link SmeltSkill#isActive(PlayerEntity)}を参照する</p>
     */
    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, blockState, blockEntity) -> {
            if (!HammerSkill.isActive(player)) {
                return true;
            }
            if (world.isClient()) {
                return true;
            }
            if (!(world instanceof ServerWorld serverWorld)) {
                return true;
            }
            
            ItemStack mainHand = player.getMainHandStack();
            // playerの手持ちのアイテムで破壊可能かどうか
            if (!mainHand.isSuitableFor(blockState)) {
//                var text =
//                        Text.of(blockState.getBlock().getTranslationKey()
//                                        + "は"
//                                        + mainHand.getName().getString()
//                                        + "では破壊できません。");
//                player.sendMessage(text, true);
                return true;
            }
            
            // 範囲を破壊
            int size = 1;
            
            // プレイヤーの視線ベクトルから最も近い方向を取得
            Vec3d lookVec = player.getRotationVector();
            Direction lookDir = Direction.getFacing(lookVec.x, lookVec.y, lookVec.z);
            BlockPos center = pos.offset(lookDir, 1);
            MinecraftServer server = world.getServer();
            
            for (int dx = -size; dx <= size; dx++) {
                for (int dy = -size; dy <= size; dy++) {
                    for (int dz = -size; dz <= size; dz++) {
                        BlockPos target = center.add(dx, dy, dz);
                        BlockState targetState = serverWorld.getBlockState(target);
                        if (targetState.isAir()) {
                            continue;
                        }
                        // ツールで破壊可能か
                        if (!mainHand.isSuitableFor(targetState)) {
                            continue;
                        }
                        
                        // ドロップ
                        Block.getDroppedStacks(targetState, serverWorld, target, serverWorld.getBlockEntity(target))
                             .forEach(stack -> Block.dropStack(serverWorld, target, stack));
                        serverWorld.setBlockState(target, Blocks.AIR.getDefaultState());
                        
                        if (!player.isCreative()) {
                            // 破壊した分ツールの耐久値を減らす
                            mainHand.damage(1, player, p -> p.sendToolBreakStatus(player.getActiveHand()));
                        }
                        
                        // 侵食
                        FantalStateManager.addFantalPollution(server, player, 1);
                    }
                }
            }
            FantalStateManager.sendFantalPollution(server, player);
            
            return false;
        });
    }
}