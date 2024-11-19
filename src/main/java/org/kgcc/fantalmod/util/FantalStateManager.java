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

public class FantalStateManager extends PersistentState {
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
    
    
    public final HashMap<UUID, PlayerFantalData> players = new HashMap<>();
    // serverが取得できない場合のために、最後の情報を保存
    public static HashMap<UUID, PlayerFantalData> lastPlayers = new HashMap<>();
    
    
    // 20 ticks = 1 seconds
    public static final int TICK_PAR_SEC = 20;
    
    public static void KeepStatusEffect(PlayerEntity player, StatusEffect effect, int amplifier, boolean ambient, boolean visible) {
        // duration（継続時間）
        // amplifier（強度）
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
    
    // 書き込み
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
    
    // 読み込み
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
        
        FantalStateManager.lastPlayers = state.players;
        
        return state;
    }
    
    // サーバーの状態を取得
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
                                                                      FantalMod.MODID);
        
        state.markDirty();
        return state;
    }
    
    public static PlayerFantalData getPlayerState(LivingEntity player) {
        var server = player.getWorld().getServer();
        if (server == null) {
//            FantalMod.LOGGER.error("Server is null");
            return FantalStateManager.lastPlayers.computeIfAbsent(player.getUuid(), uuid -> new PlayerFantalData());
        }
        
        FantalStateManager serverState = getServerState(server);
        
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
}
