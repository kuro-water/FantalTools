package org.kgcc.fantalmod.test;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.kgcc.fantalmod.FantalMod;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

public class RecallDataManager extends PersistentState {
    private static final String RECALL_DATA_LIST_KEY = "recallDataList";
    private static final int MAX_RECORD_NUM = 40;
    
    /**
     * プレイヤーごとのリコールデータ
     * UUIDをキーにして、リコールデータ（RecallData）を管理する
     */
    public final HashMap<UUID, LinkedList<RecallData>> players = new HashMap<>();
    
    /**
     * NBTに書き込み
     */
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound playersNbt = new NbtCompound();
        players.forEach((uuid, playerData) -> {
            // playerData というLinkedListをNbtCompoundに変換
            NbtCompound recallDataListNbt = new NbtCompound();
            for (int i = 0; i < playerData.size(); i++) {
                recallDataListNbt.put(RECALL_DATA_LIST_KEY + i, playerData.get(i).toNbt());
            }
            playersNbt.put(uuid.toString(), recallDataListNbt);
        });
        nbt.put("players", playersNbt);
        return nbt;
    }
    
    /**
     * NBTから読み込み
     */
    public static RecallDataManager createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        RecallDataManager state = new RecallDataManager();
        NbtCompound playersNbt = tag.getCompound(RECALL_DATA_LIST_KEY);
        playersNbt.getKeys().forEach(key -> {
            LinkedList<RecallData> recallDataList = new LinkedList<>();
            NbtCompound playerNbt = playersNbt.getCompound(key);
            for (int i = 0; i < playerNbt.getKeys().size(); i++) {
                recallDataList.add(RecallData.fromNbt(playerNbt.getCompound("recallData" + i)));
            }
            
            UUID uuid = UUID.fromString(key);
            state.players.put(uuid, recallDataList);
        });
        
        return state;
    }
    
    /**
     * サーバーの状態を取得
     * RecallDataManagerのserver情報を保持したインスタンスを取得する？
     * プレイヤーごとのRecallDataしか情報が無いので、あまり使うことは多くないと思う
     */
    public static RecallDataManager getServerState(MinecraftServer server) {
        var world = server.getWorld(World.OVERWORLD);
        if (world == null) {
            throw new IllegalStateException("World is null");
        }
        PersistentStateManager persistentStateManager = world.getPersistentStateManager();
        
        RecallDataManager state = persistentStateManager.getOrCreate(
                nbt -> createFromNbt(nbt, null),
                // Create from NBT
                RecallDataManager::new,
                // Create new if not present
                FantalMod.MODID + "_recall_data_manager");
        
        state.markDirty();
        return state;
    }
    
    /**
     * プレイヤーのリコールデータを取得する
     * markDirty()を呼び出す必要があるのでprivateにしている
     * @param player
     * @return
     */
    private static LinkedList<RecallData> getPlayerRecallData(LivingEntity player) {
        var world = player.getWorld().getServer();
        if (world == null) {
            throw new IllegalStateException("World is null");
        }
        RecallDataManager serverState = getServerState(world);
        
        // uuid でプレイヤーを取得するか、プレイヤーのデータがまだない場合は、新しいプレイヤー状態を作成します
        return serverState.players.computeIfAbsent(player.getUuid(), uuid -> new LinkedList<>());
    }
    
    /**
     * LinkedList<RecallData>をクライアントサイドに送信する
     * <p>テレポート処理はサーバーサイドなので、使うことはないと思う</p>
     * <pre>{@code // 受信はClientのinitializeメソッドでこんな感じでする
     * ClientPlayNetworking.registerGlobalReceiver(FantalMod.FANTAL_POLLUTION, (client, handler, buf, responseSender) -> {
     *     // データを読み取る
     *     int totalFantalPollution = buf.readInt();
     *     int playerFantalPollution = buf.readInt();
     *
     *     // クライアントスレッドで処理
     *     client.execute(() -> {
     *         // データをクライアント側で確認または表示
     *         System.out.println("Total Fantal Pollution: " + totalFantalPollution);
     *         System.out.println("Player Fantal Pollution: " + playerFantalPollution);
     *     });
     * });
     * }</pre>
     */
    public static void sendRecallDataList(MinecraftServer server, PlayerEntity user) {
        ServerPlayerEntity playerEntity = server.getPlayerManager().getPlayer(user.getUuid());
        if (playerEntity == null) {
            throw new IllegalStateException("Player is null");
        }
        
        // クライアントに送信
        PacketByteBuf data = PacketByteBufs.create();
        LinkedList<RecallData> playerState = RecallDataManager.getPlayerRecallData(user);
        playerState.forEach(recallData -> {
            // 送信するデータをPacketByteBufに変換
            recallData.toPacket(data);
        });
        server.execute(() -> {
            FantalMod.LOGGER.info("Sending pollution data to client");
            ServerPlayNetworking.send(playerEntity, FantalMod.RECALL_DATA, data);
        });
    }
    
    /**
     * プレイヤーのリコールデータを追加する
     */
    public static void add(PlayerEntity player, RecallData recallData) {
        if (player.getServer() == null) {
            return;
        }
        if (recallData.health <= 0) {
            // HPが0以下のデータは保存しない
            return;
        }
        
        // LinkedListは参照型なので、直接操作する
        LinkedList<RecallData> recallDataList = getPlayerRecallData(player);
        
        // 記録が最大tick数を超えたら古い記録を削除
        if (recallDataList.size() >= MAX_RECORD_NUM) {
            recallDataList.removeFirst();
        }
        // 記録
        recallDataList.add(recallData);
        
        // マルチスレッド環境でのデータ競合を避けるために、サーバーの状態をマークする
        RecallDataManager.getServerState(player.getServer()).markDirty();
    }
    
    /**
     * プレイヤーのリコールデータの最後を取り出し、削除する
     */
    public static RecallData removeLast(PlayerEntity player) {
        LinkedList<RecallData> recallDataList = getPlayerRecallData(player);
        if (recallDataList.isEmpty() || player.getServer() == null) {
            return null;
        }
        
        var data = recallDataList.removeLast();
        // マルチスレッド環境でのデータ競合を避けるために、サーバーの状態をマークする
        RecallDataManager.getServerState(player.getServer()).markDirty();
        return data;
    }
    
    /**
     * プレイヤーのリコールデータをクリアする
     */
    public static void clear(PlayerEntity player) {
        if (player.getServer() == null) {
            return;
        }
        // LinkedListは参照型なので、直接操作する
        LinkedList<RecallData> recallDataList = getPlayerRecallData(player);
        
        // 記録をクリア
        recallDataList.clear();
        
        // マルチスレッド環境でのデータ競合を避けるために、サーバーの状態をマークする
        RecallDataManager.getServerState(player.getServer()).markDirty();
    }
    
    public static boolean isEmpty(LivingEntity player) {
        // プレイヤーのリコールデータが空かどうかを確認する
        return getPlayerRecallData(player).isEmpty();
    }
    
    public static RecallData getFirst(LivingEntity player) {
        // プレイヤーのリコールデータの最初の要素を取得する
        LinkedList<RecallData> recallDataList = getPlayerRecallData(player);
        return recallDataList.getFirst();
    }
    
    public static int size(LivingEntity player) {
        // プレイヤーのリコールデータのサイズを取得する
        LinkedList<RecallData> recallDataList = getPlayerRecallData(player);
        return recallDataList.size();
    }
    
    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof PlayerEntity player) {
                // プレイヤーが死亡したとき記録を消去
                // これだけだと死亡後HP0のデータが残ってしまうので、
                // HP0のデータはそもそも保存しないようにすること
                RecallDataManager.clear(player);
            }
        });
    }
}
