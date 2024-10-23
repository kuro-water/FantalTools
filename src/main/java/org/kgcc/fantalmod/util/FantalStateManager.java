package org.kgcc.fantalmod.util;

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
    public int totalFantalPollution = 0;
    public final HashMap<UUID, PlayerFantalData> players = new HashMap<>();
    
    public static final int TICK_PAR_SEC = 20;
    
    public static void KeepStatusEffect(PlayerEntity player, StatusEffect effect) {
        // duration（継続時間）: 20 ticks = 1 seconds
        // amplifier（強度）
        try {
            // effectの残り時間をチェック
            var hasteDuration = Objects.requireNonNull(player.getStatusEffect(effect)).getDuration();
            if (hasteDuration < 5 * TICK_PAR_SEC) {
                player.addStatusEffect(new StatusEffectInstance(effect, 10 * TICK_PAR_SEC, 0, false, false));
            }
        } catch (NullPointerException _e) {
            // effectが付与されていない場合NullPointerExceptionが発生するので、こちらで再度付与
            player.addStatusEffect(new StatusEffectInstance(effect, 10 * TICK_PAR_SEC, 0, false, false));
        }
    }
    
    // 毎tickごとにポーション効果をチェックし、かけなおす。
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                var playerState = FantalStateManager.getPlayerState(player);
                if (20 < playerState.fantalPollution) {
                    KeepStatusEffect(player, StatusEffects.HUNGER);
                }
                if (40 < playerState.fantalPollution) {
                    KeepStatusEffect(player, StatusEffects.SLOWNESS);
                }
                if (60 < playerState.fantalPollution) {
                    KeepStatusEffect(player, StatusEffects.MINING_FATIGUE);
                }
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
            
            playerNbt.putInt("fantalPollution", playerData.fantalPollution);
            
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
            
            playerData.fantalPollution = playersNbt.getCompound(key).getInt("fantalPollution");
            
            UUID uuid = UUID.fromString(key);
            state.players.put(uuid, playerData);
        });
        
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
        var world = player.getWorld().getServer();
        if (world == null) {
            throw new IllegalStateException("World is null");
        }
        FantalStateManager serverState = getServerState(world);
        
        // uuid でプレイヤーを取得するか、プレイヤーのデータがまだない場合は、新しいプレイヤー状態を作成します
        return serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerFantalData());
    }
    
    public static void sendFantalPollution(World world, PlayerEntity user) {
        MinecraftServer server = world.getServer();
        if (server == null) {
            throw new IllegalStateException("Server is null");
        }
        FantalStateManager serverState = FantalStateManager.getServerState(server);
        ServerPlayerEntity playerEntity = server.getPlayerManager().getPlayer(user.getUuid());
        if (playerEntity == null) {
            throw new IllegalStateException("Player is null");
        }
        
        // クライアントに送信
        PacketByteBuf data = PacketByteBufs.create();
        PlayerFantalData playerState = FantalStateManager.getPlayerState(user);
        data.writeInt(serverState.totalFantalPollution);
        data.writeInt(playerState.fantalPollution);
        server.execute(() -> {
            FantalMod.LOGGER.info("Sending pollution data to client");
            ServerPlayNetworking.send(playerEntity, FantalMod.FANTAL_POLLUTION, data);
        });
    }
    
    public static void addFantalPollution(World world, PlayerEntity user, int dif) {
        setServerFantalPollution(world, getServerState(
                Objects.requireNonNull(world.getServer())).totalFantalPollution + dif);
        setFantalPollution(user, getPlayerState(user).fantalPollution + dif);
    }
    
    public static void setFantalPollution(PlayerEntity user, int value) {
        PlayerFantalData playerState = FantalStateManager.getPlayerState(user);
        playerState.fantalPollution = value;
    }
    
    public static void setServerFantalPollution(World world, int value) {
        MinecraftServer server = world.getServer();
        if (server == null) {
            throw new IllegalStateException("Server is null");
        }
        FantalStateManager serverState = FantalStateManager.getServerState(server);
        serverState.totalFantalPollution = value;
        
    }
}
