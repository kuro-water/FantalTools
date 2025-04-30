package org.kgcc.fantalmod.test;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

public class Recall {
    private Boolean isRecalling = false;
    
    /**
     * プレイヤーの位置、角度、体力を記録
     * 該当アイテムのinventoryTickで呼び出す
     */
    public void record(PlayerEntity playerEntity) {
        // 記録が無くなっていればリコールを終了
        if (RecallDataManager.isEmpty(playerEntity)) {
            isRecalling = false;
        }
        // リコール中は記録を行わない
        if (isRecalling || playerEntity.getServer() == null) {
            return;
        }
        
        RecallDataManager.add(playerEntity, new RecallData(playerEntity));
    }
    
    /**
     * テレポート先が安全かどうか（窒息するかどうか）を確認する
     *
     * @param world ワールド
     * @param pos   テレポート先の位置
     * @return 安全な場合はtrue
     */
    private boolean isSafeLocation(World world, BlockPos pos) {
        // プレイヤーの体が入る2ブロック分の空間をチェック
        BlockPos headPos = pos.up();
        
        BlockState footState = world.getBlockState(pos);
        BlockState headState = world.getBlockState(headPos);
        
        // 窒息するブロックかどうか
        return !(footState.shouldSuffocate(world, pos) ||
                headState.shouldSuffocate(world, headPos));
    }
    
    
    /**
     * リコールを行う
     * サーバーにそこそこの処理速度が無いとカクカクになる。
     * 毎tick実行だからしょうがないかなぁ
     *
     * @param server       サーバー
     * @param playerEntity サーバーサイドのプレイヤーのみ
     * @return リコールする回数
     */
    public int recall(@NotNull MinecraftServer server, PlayerEntity playerEntity) {
        // todo: FantalStateManagerもリファクタしたい。名前とか。
        // todo: 耐久値ガンガン削っていこう
        // todo: 松明設置じゃなくて独自の光源ほしいな。光るクリスタル
        // todo: もしかしてFantalPollutionオーバーワールドでしか機能してない？
        // todo: リコールを滑らかにしたい tp以外の方法ないかな
        // todo: ->トレーサーのリコールは座標だけ追従で、視点は現在の視点からリコール後の視点まで移動するだけ。leapで実装できそう
        // todo: インデント直さねば
        // todo: requireNonNullよりもif文でnullチェックしたほうが良いかも
        // todo: ただしサーバサイドでnullでない場合にはrequireNonNullのほうが良い？
        // todo: 地面に埋まりそう
        // todo: 連打してるとリコールできなくなるバグある？
        // todo: リコール時間の調整
        
        var world = playerEntity.getWorld();
        // クライアントサイドでは処理しない
        if (world.isClient() || isRecalling) {
            return 0;
        }
        
        isRecalling = true;
        var startData = new RecallData(playerEntity);
        var targetData = RecallDataManager.getFirst(playerEntity);
        var targetNum = RecallDataManager.size(playerEntity);

//        FantalMod.LOGGER.info("Recalling...");
        TickHandler.startTask(targetNum, () -> {
            var data = RecallDataManager.removeLast(playerEntity);
//            RecallDataManager.removeLast(playerEntity);
//            FantalMod.LOGGER.info("Recalling... {}", data);
            if (data == null) {
                return;
            }
            
            if (!isSafeLocation(data.getWorld(server), BlockPos.ofFloored(data.pos))) {
                // プレイヤーに警告メッセージを送信
                if (playerEntity instanceof ServerPlayerEntity serverPlayer) {
                    serverPlayer.sendMessage(Text.literal("テレポート先が安全ではありません！"), false);
                }
                return;
            }
            
            
            var delta = (float) (targetNum - RecallDataManager.size(playerEntity)) / targetNum;
            
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
                        startData.yaw + delta * MathHelper.wrapDegrees(targetData.yaw - startData.yaw), // lerpみたいな感じで補間
                        MathHelper.lerp(delta, startData.pitch, targetData.pitch));
            } else {
                // よくわからんけどフラグが必要なので用意
                Set<PositionFlag> flags = EnumSet.noneOf(PositionFlag.class);
                playerEntity.teleport(
                        data.getWorld(server),
                        data.pos.x,
                        data.pos.y,
                        data.pos.z,
                        flags,
                        startData.yaw + delta * MathHelper.wrapDegrees(targetData.yaw - startData.yaw), // lerpみたいな感じで補間
                        MathHelper.lerp(delta, startData.pitch, targetData.pitch));
            }
            playerEntity.setHealth(data.health); // HPを復元
        });
        return RecallDataManager.size(playerEntity);
    }
}