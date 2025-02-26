package org.kgcc.fantalmod.test;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.kgcc.fantalmod.FantalMod;

import java.util.LinkedList;

public class PlayerRecall {
    // リコールの最大tick数
    private static final int MAX_TICKS = 40;
    
    private final LinkedList<Vec3d> positions = new LinkedList<>();
    // yaw：水平角度（左右）
    private final LinkedList<Float> yaws = new LinkedList<>();
    // pitch：垂直角度（上下）
    private final LinkedList<Float> pitches = new LinkedList<>();
    private final LinkedList<Float> healths = new LinkedList<>();
    
    private Boolean isRecalling = false;
    
    public void record(PlayerEntity player) {
        // 記録が無くなっていればリコールを終了
        if (positions.isEmpty()) {
            isRecalling = false;
        }
        // リコール中は記録を行わない
        if (isRecalling) {
            return;
        }
        
        // 記録が最大tick数を超えたら古い記録を削除
        if (positions.size() >= MAX_TICKS) {
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
        FantalMod.LOGGER.info("Recorded: {}", positions);
    }
    
    public void recall(PlayerEntity player) {
        isRecalling = true;
        FantalMod.LOGGER.info("Recalling...");
        TickHandler.startTask(MAX_TICKS, () -> {
            if (!positions.isEmpty()) {
                FantalMod.LOGGER.info("Recalling... {}", positions.size());
                player.setPosition(positions.removeLast());
                player.setYaw(yaws.removeLast());
                player.setPitch(pitches.removeLast());
                player.setHealth(healths.removeLast());
            }
        });
    }
}
