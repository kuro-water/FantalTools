package org.kgcc.fantalmod.test;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.kgcc.fantalmod.FantalMod;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Set;

public class PlayerRecall {
    // リコールの最大tick数
    private static final int MAX_RECORD_NUM = 40;
    
    private final LinkedList<Vec3d> positions = new LinkedList<>();
    // yaw：水平角度（左右）
    private final LinkedList<Float> yaws = new LinkedList<>();
    // pitch：垂直角度（上下）
    private final LinkedList<Float> pitches = new LinkedList<>();
    private final LinkedList<Float> healths = new LinkedList<>();
    
    private Boolean isRecalling = false;
    
    /**
     * プレイヤーの位置、角度、体力を記録
     * 2tickに1回記録
     */
    public void record(PlayerEntity player) {
        // 記録が無くなっていればリコールを終了
        if (positions.isEmpty()) {
            isRecalling = false;
        }
        // リコール中は記録を行わない
        if (isRecalling) {
            return;
        }
        if (player.getServer() == null) {
            return;
        }
        // 2tickに1回記録
        if (player.getServer().getTicks() % 2 == 0) {
            return;
        }
        
        
        // 記録が最大tick数を超えたら古い記録を削除
        if (positions.size() >= MAX_RECORD_NUM) {
            positions.removeFirst();
            yaws.removeFirst();
            pitches.removeFirst();
            healths.removeFirst();
        }
        
        // 現在のプレイヤーの位置、角度、体力を記録
        positions.addLast(player.getPos());
        yaws.addLast(player.getYaw());
        pitches.addLast(player.getPitch());
        healths.addLast(player.getHealth());
//        FantalMod.LOGGER.info("Recorded: {}", positions);
    }
    
    public void recall(PlayerEntity player) {
        isRecalling = true;
        FantalMod.LOGGER.info("Recalling...");
        TickHandler.startTask(MAX_RECORD_NUM, () -> {
            if (!positions.isEmpty()) {
                FantalMod.LOGGER.info("Recalling... {}:{}", positions.size(), positions);
                var pos = positions.removeLast();
                var world = player.getEntityWorld();
                Set<PositionFlag> flags = EnumSet.noneOf(PositionFlag.class);
                player.teleport((ServerWorld) world, pos.x, pos.y, pos.z, flags, yaws.removeLast(),
                                pitches.removeLast()); // 座標と視点を復元
                
                player.setHealth(healths.removeLast()); // HPを復元
            }
        });
//        Vec3d pos = new Vec3d(0, 30, 0);
//        player.teleport(pos.x, pos.y, pos.z); // 座標だけ移動
//        var world = player.getEntityWorld();
//        Set<PositionFlag> flags = EnumSet.noneOf(PositionFlag.class);
//        player.teleport((ServerWorld) world, pos.x, pos.y, pos.z, flags, 0, 0); // 座標と視点を移動
    }
}
