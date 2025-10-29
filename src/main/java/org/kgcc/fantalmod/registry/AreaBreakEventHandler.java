package org.kgcc.fantalmod.registry;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.kgcc.fantalmod.skill.HammerSkill;
import org.kgcc.fantalmod.tool.FantalToolItem;
import org.kgcc.fantalmod.util.FantalStateManager;

public class AreaBreakEventHandler {
    /**
     * <p>ブロック破壊時に範囲破壊スキルが有効なプレイヤーの場合、
     * 範囲破壊を行うイベントハンドラ</p>
     */
    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, blockState, blockEntity) -> {
            if (world.isClient())
                return true;
            if (!(world instanceof ServerWorld serverWorld))
                return true;
            
            ItemStack mainHand = player.getMainHandStack();
            String skillName = FantalToolItem.readNbt(mainHand);
            if (!Text.translatable("skill.fantalmod.hammer").getString().equals(skillName)) {
//                player.sendMessage(Text.literal("§cこのツールにはHammerスキルが付いていません"), true);
                return true;
            }
            
            
            // プレイヤーがON状態でなければ通常破壊
            if (!HammerSkill.isActive(player)) {
                return true;
            }
            
            // ツールで破壊可能かどうか
            if (!mainHand.isSuitableFor(blockState)) {
                return true;
            }
            
            // 範囲破壊処理
            int size = 1;
            Vec3d lookVec = player.getRotationVector();
            Direction lookDir = Direction.getFacing(lookVec.x, lookVec.y, lookVec.z);
            BlockPos center = pos.offset(lookDir, 1);
            MinecraftServer server = world.getServer();
            
            for (int dx = -size; dx <= size; dx++) {
                for (int dy = -size; dy <= size; dy++) {
                    for (int dz = -size; dz <= size; dz++) {
                        BlockPos target = center.add(dx, dy, dz);
                        BlockState targetState = serverWorld.getBlockState(target);
                        if (targetState.isAir())
                            continue;
                        if (!mainHand.isSuitableFor(targetState))
                            continue;
                        
                        Block.getDroppedStacks(targetState, serverWorld, target, serverWorld.getBlockEntity(target))
                             .forEach(stack -> Block.dropStack(serverWorld, target, stack));
                        serverWorld.setBlockState(target, Blocks.AIR.getDefaultState());
                    }
                }
            }
            
            FantalStateManager.addFantalPollution(server, player, 3);
            return false; // 通常破壊をキャンセル（範囲破壊済み）
        });
    }
}
