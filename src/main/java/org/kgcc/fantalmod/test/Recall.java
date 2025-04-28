package org.kgcc.fantalmod.test;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

import static org.kgcc.fantalmod.test.RecallDataManager.getPlayerRecallData;

public class Recall {
    /**
     * リコール用のデータを保持する
     * Firstが一番古く、Lastが一番新しい
     */
//    private final LinkedList<RecallData> recallDataList = new LinkedList<>();
    
    private Boolean isRecalling = false;
    
    /**
     * プレイヤーの位置、角度、体力を記録
     * 2tickに1回記録
     */
    public void record(PlayerEntity playerEntity) {
        // 記録が無くなっていればリコールを終了
        if (getPlayerRecallData(playerEntity).isEmpty()) {
            isRecalling = false;
        }
        // リコール中は記録を行わない
        if (isRecalling) {
            return;
        }
        if (playerEntity.getServer() == null) {
            return;
        }
        
        RecallDataManager.addRecallData(playerEntity, new RecallData(playerEntity));
//        FantalMod.LOGGER.info("Recorded: {}", getPlayerRecallData(playerEntity).getLast());
    }
    
    /**
     * リコールを行う
     * サーバーにそこそこの処理速度が無いとカクカクになる。
     * 毎tick実行だからしょうがないかなぁ
     *
     * @param playerEntity サーバーサイドのプレイヤーのみ
     */
    public void recall(@NotNull MinecraftServer server, PlayerEntity playerEntity) {
        // todo: FantalStateManagerもリファクタしたい。名前とか。
        // todo: 耐久値ガンガン削っていこう
        // todo: 松明設置じゃなくて独自の光源ほしいな。光るクリスタル
        // todo: 死んだら履歴リセットしよう
        // todo: もしかしてFantalPollutionオーバーワールドでしか機能してない？
        // todo: リコールを滑らかにしたい tp以外の方法ないかな
        // todo: ->トレーサーのリコールは座標だけ追従で、視点は現在の視点からリコール後の視点まで移動するだけ。leapで実装できそう
        
        var world = playerEntity.getWorld();
        // クライアントサイドでは処理しない
        if (world.isClient()) {
            return;
        }
        if (isRecalling) {
//            FantalMod.LOGGER.info("Already recalling...");
            return;
        }
        
        isRecalling = true;
//        FantalMod.LOGGER.info("Recalling...");
        TickHandler.startTask(getPlayerRecallData(playerEntity).size(), () -> {
            var data = RecallDataManager.getLastRecallData(playerEntity);
//            FantalMod.LOGGER.info("Recalling... {}", data);
            if (data == null) {
                return;
            }
            
            /*
             * 参考：https://www.youtube.com/watch?v=Wiufoa-BSCM&list=WL&index=28&t=1s
             * ----- by GitHub Copilot -----
             * 同じワールド内のテレポート (requestTeleport)
             * requestTeleportは、同じワールド内でのプレイヤーの位置や視点を即座に変更するための効率的なメソッドです。
             * ワールド間のデータ転送やエンティティの再登録が不要で、軽量な処理で済みます。
             * 異なるワールド間のテレポート (teleport)
             * 異なるワールド間でのテレポートでは、プレイヤーのデータを現在のワールドから削除し、新しいワールドに再登録する必要があります。
             * teleportメソッドは、このようなワールド間の移動に必要な処理（エンティティの再登録やワールドの切り替え）を適切に行います。
             */
            if (world.getRegistryKey().equals(data.getDimensionKey())) {
                ((ServerPlayerEntity) playerEntity).networkHandler.requestTeleport(
                        data.pos.x,
                        data.pos.y,
                        data.pos.z,
                        data.yaw,
                        data.pitch);
            } else {
                // よくわからんけどフラグが必要なので用意
                Set<PositionFlag> flags = EnumSet.noneOf(PositionFlag.class);
                playerEntity.teleport(
                        data.getWorld(server),
                        data.pos.x,
                        data.pos.y,
                        data.pos.z,
                        flags,
                        data.yaw,
                        data.pitch);
            }
            playerEntity.setHealth(data.health); // HPを復元
            
        });
    }
}