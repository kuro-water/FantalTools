package org.kgcc.fantalmod.skill;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.kgcc.fantalmod.recall.RecallData;
import org.kgcc.fantalmod.recall.RecallDataManager;
import org.kgcc.fantalmod.util.FantalStateManager;
import org.kgcc.fantalmod.util.ServerTickHandler;

import java.util.*;

public class RecallSkill implements BaseSkill {
    private final List<Tool> TOOLS = List.of(Tool.PICKAXE, Tool.AXE, Tool.SHOVEL, Tool.HOE, Tool.SWORD);
    
    @Override
    public List<Tool> getTools() {
        return TOOLS;
    }
    
    private static final Set<UUID> isRecallingSet = new HashSet<>();
    
    /**
     * <p>プレイヤーの位置、角度、体力を記録</p>
     * <p>該当アイテムのinventoryTickで呼び出す</p>
     */
    public static void record(PlayerEntity playerEntity) {
        UUID uuid = playerEntity.getUuid();
        // 記録が無くなっていればリコールを終了
        if (RecallDataManager.isEmpty(playerEntity)) {
            isRecallingSet.remove(uuid);
        }
        // リコール中は記録を行わない
        if (playerEntity.getServer() == null || isRecallingSet.contains(uuid)) {
            return;
        }
        
        RecallDataManager.add(playerEntity, new RecallData(playerEntity));
    }
    
    /**
     * <p>テレポート先が安全かどうか（窒息するかどうか）を確認する</p>
     *
     * @param world ワールド
     * @param pos   テレポート先の位置
     * @return <p>安全な場合はtrue</p>
     */
    private static boolean isSafeLocation(World world, BlockPos pos) {
        // プレイヤーの体が入る2ブロック分の空間をチェック
        BlockPos headPos = pos.up();
        
        BlockState footState = world.getBlockState(pos);
        BlockState headState = world.getBlockState(headPos);
        
        // 窒息するブロックかどうか
        return !(footState.shouldSuffocate(world, pos) ||
                headState.shouldSuffocate(world, headPos));
    }
    
    /**
     * <p>リコールを行う</p>
     * <p>サーバーにそこそこの処理速度が無いとカクカクになる。</p>
     * <p>毎tick実行だからしょうがないかなぁ</p>
     *
     * @param server       サーバー
     * @param playerEntity サーバーサイドのプレイヤーのみ
     * @return <p>リコールする回数</p>
     */
    public static int recall(@NotNull MinecraftServer server, PlayerEntity playerEntity) {
        // todo: FantalStateManagerもリファクタしたい。名前とか。
        // todo: 松明設置じゃなくて独自の光源ほしいな。光るクリスタル
        // todo: もしかしてFantalPollutionオーバーワールドでしか機能してない？
        
        var world = playerEntity.getWorld();
        // クライアントサイドでは処理しない
        if (world.isClient() || isRecallingSet.contains(playerEntity.getUuid())) {
            return 0;
        }
        
        isRecallingSet.add(playerEntity.getUuid());
        var startData = new RecallData(playerEntity);
        var targetData = RecallDataManager.getFirst(playerEntity);
        var targetNum = RecallDataManager.size(playerEntity);
        
        ServerTickHandler.startTask(targetNum, () -> {
            var data = RecallDataManager.removeLast(playerEntity);
            if (data == null) {
                return;
            }
            
            if (!isSafeLocation(data.getWorld(server), BlockPos.ofFloored(data.pos))) {
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
            playerEntity.fallDistance = 0;
        });
        return RecallDataManager.size(playerEntity);
    }
    
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // サーバー上のすべてのプレイヤーに対して処理
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                record(player);
            }
        });
    }
    
    @Override
    public String getTranslationKey() {
        return "recall";
    }
    
    @Override
    public MutableText getName() {
        return Text.translatable("skill.fantalmod.recall");
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        var server = Objects.requireNonNull(world.getServer());
        int amount = recall(server, user);
        
        FantalStateManager.addFantalPollution(server, user, amount / 4);
        FantalStateManager.sendFantalPollution(server, user);

//        if (!user.isCreative()) {
//            // 耐久値を減らす
//            ItemStack stack = user.getStackInHand(hand);
//            stack.damage(amount, user, (e) -> e.sendToolBreakStatus(hand));
//        }
//
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
