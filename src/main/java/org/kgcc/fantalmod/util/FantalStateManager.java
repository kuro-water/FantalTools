package org.kgcc.fantalmod.util;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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
import java.util.Objects;
import java.util.UUID;

/**
 * PlayerFantalDataを管理するクラス
 * NBTに保存したりとかのメソッドが用意されている
 */
public class FantalStateManager extends PersistentState {
    /**
     * サーバー全体の汚染度
     * 汚染度は、プレイヤーの汚染度の合計値のはず
     * ただ、減らす処理とか同期とか抜けてるかも。計算合わない気がしてきた
     */
    private int totalFantalPollution = 0;



    public int getTotalFantalPollution() {
        return totalFantalPollution;
    }
    
    public void setTotalFantalPollution(int totalFantalPollution) {
        if (totalFantalPollution < 0) {
            totalFantalPollution = 0;
        }
        this.totalFantalPollution = totalFantalPollution;
    }
    
    /**
     * プレイヤーごとの汚染度
     * UUIDをキーにして、汚染度（PlayerFantalData）を管理する
     */
    public final HashMap<UUID, PlayerFantalData> players = new HashMap<>();
    
    /**
     * 20 ticks = 1 seconds
     */
    public static final int TICK_PAR_SEC = 20;
    
    /**
     * プレイヤーに状態異常を付与する
     * 状態異常の継続時間が5秒未満の場合、10秒の状態異常を付与する
     * ServerTickEvents.END_SERVER_TICKなどで毎tick呼び出される前提
     * FantalStateManager.register()で登録される
     *
     * @param player
     * @param effect
     * @param amplifier 強度
     * @param ambient
     * @param visible
     */
    public static void KeepStatusEffect(PlayerEntity player, StatusEffect effect, int amplifier, boolean ambient, boolean visible) {
        // duration：継続時間
        try {
            // effectの残り時間をチェック
            var hasteDuration = Objects.requireNonNull(player.getStatusEffect(effect)).getDuration();
            if (hasteDuration < 5 * TICK_PAR_SEC) {
                player.addStatusEffect(
                        new StatusEffectInstance(effect, 10 * TICK_PAR_SEC, amplifier, ambient, visible));
            }
        } catch (NullPointerException _e) {
            // effectが付与されていない場合NullPointerExceptionが発生するので、こちらで再度付与
            player.addStatusEffect(new StatusEffectInstance(effect, 10 * TICK_PAR_SEC, amplifier, ambient, visible));
        }
    }
    
    public static int lastTick = 0;
    
    /**
     * 毎tickごとに必要な処理と、死亡時に必要な処理をMinecraftに存在するEventたちに登録する
     */
    public static void register() {
        // 毎tickごとにチェック
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // 20秒経過していれば、全プレイヤーの汚染度を1減らす
            var tick = server.getTicks();
            if (20 * TICK_PAR_SEC < tick - lastTick) {
                lastTick = tick;
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    addFantalPollution(server, player, -1);
                }
            }
            
            // 汚染度による状態異常を付与
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                var playerState = FantalStateManager.getPlayerState(player);
                if (20 < playerState.getFantalPollution()) {
                    KeepStatusEffect(player, StatusEffects.HUNGER, 0, false, false);
                }
                if (40 < playerState.getFantalPollution()) {
                    KeepStatusEffect(player, StatusEffects.SLOWNESS, 0, false, false);
                }
                if (60 < playerState.getFantalPollution()) {
                    KeepStatusEffect(player, StatusEffects.MINING_FATIGUE, 0, false, false);
                }
                if (80 < playerState.getFantalPollution()) {
                    KeepStatusEffect(player, StatusEffects.WEAKNESS, 0, false, false);
                }
                if (100 < playerState.getFantalPollution()) {
                    KeepStatusEffect(player, StatusEffects.POISON, 0, false, false);
                }
                if (150 < playerState.getFantalPollution()) {
                    KeepStatusEffect(player, StatusEffects.WITHER, 1, false, false);
                }
                if (200 < playerState.getFantalPollution()) {
                    player.kill();
                }
            }
        });
        
        // プレイヤーが死亡したときに汚染度をリセット
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof PlayerEntity player) {
                FantalStateManager.setFantalPollution(player, 0);
            }
        });
    }
    
    /**
     * NBTに書き込み
     */
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("totalFantalPollution", totalFantalPollution);
        
        NbtCompound playersNbt = new NbtCompound();
        players.forEach((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();
            playerNbt.putInt("fantalPollution", playerData.getFantalPollution());
            playersNbt.put(uuid.toString(), playerNbt);
        });
        nbt.put("players", playersNbt);
        return nbt;
    }
    
    /**
     * NBTから読み込み
     *
     * @param tag
     * @param registryLookup
     * @return
     */
    public static FantalStateManager createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        FantalStateManager state = new FantalStateManager();
        state.totalFantalPollution = tag.getInt("totalFantalPollution");
        
        NbtCompound playersNbt = tag.getCompound("players");
        playersNbt.getKeys().forEach(key -> {
            PlayerFantalData playerData = new PlayerFantalData();
            
            playerData.setFantalPollution(playersNbt.getCompound(key).getInt("fantalPollution"));
            
            UUID uuid = UUID.fromString(key);
            state.players.put(uuid, playerData);
        });
        
        return state;
    }
    
    /**
     * サーバーの状態を取得
     *
     * @param server
     * @return
     */
    public static FantalStateManager getServerState(MinecraftServer server) {
        var world = server.getWorld(World.OVERWORLD);
        if (world == null) {
            throw new IllegalStateException("World is null");
        }
        PersistentStateManager persistentStateManager = world.getPersistentStateManager();
        
        FantalStateManager state = persistentStateManager.getOrCreate(nbt -> createFromNbt(nbt, null),
                                                                      // Create from NBT
                                                                      FantalStateManager::new,
                                                                      // Create new if not present
                                                                      FantalMod.MODID + "_fantal_state_manager");
        
        state.markDirty();
        return state;
    }
    
    public static PlayerFantalData getPlayerState(LivingEntity player) {
        var world = player.getWorld().getServer();
        if (world == null) {
            throw new IllegalStateException("World is null");
        }
        FantalStateManager serverState = getServerState(world);
        
        // uuid でプレイヤーを取得するか、プレイヤーのデータがまだない場合は、新しいプレイヤー状態を作成します
        return serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerFantalData());
    }
    
    public static void sendFantalPollution(MinecraftServer server, PlayerEntity user) {
        FantalStateManager serverState = FantalStateManager.getServerState(server);
        ServerPlayerEntity playerEntity = server.getPlayerManager().getPlayer(user.getUuid());
        if (playerEntity == null) {
            throw new IllegalStateException("Player is null");
        }
        
        // クライアントに送信
        PacketByteBuf data = PacketByteBufs.create();
        PlayerFantalData playerState = FantalStateManager.getPlayerState(user);
        data.writeInt(serverState.totalFantalPollution);
        data.writeInt(playerState.getFantalPollution());
        server.execute(() -> {
            FantalMod.LOGGER.info("Sending pollution data to client");
            ServerPlayNetworking.send(playerEntity, FantalMod.FANTAL_POLLUTION, data);
        });
    }
    
    public static void addFantalPollution(MinecraftServer server, PlayerEntity user, int dif) {
        setServerFantalPollution(server, getServerState(server).totalFantalPollution + dif);
        setFantalPollution(user, getPlayerState(user).getFantalPollution() + dif);
    }
    
    public static void setFantalPollution(PlayerEntity user, int value) {
        PlayerFantalData playerState = FantalStateManager.getPlayerState(user);
        playerState.setFantalPollution(value);
    }
    
    public static void setServerFantalPollution(MinecraftServer server, int value) {
        FantalStateManager serverState = FantalStateManager.getServerState(server);
        serverState.setTotalFantalPollution(value);
    }

    //==========================================================================
    // swordEffectEnabled フラグ
    private static boolean swordEffectEnabled = false;

    // isSwordEffectEnabled メソッド
    public static boolean isSwordEffectEnabled() {
        return swordEffectEnabled;
    }

    // setSwordEffectEnabled メソッド
    public static void setSwordEffectEnabled(boolean enabled) {
        swordEffectEnabled = enabled;
    }
}
