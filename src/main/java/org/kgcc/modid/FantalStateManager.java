package org.kgcc.modid;

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

import java.util.HashMap;
import java.util.UUID;

public class FantalStateManager extends PersistentState {
    public Integer totalFantalPollution = 0;
    public HashMap<UUID, PlayerFantalData> players = new HashMap<>();

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
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        FantalStateManager state = persistentStateManager.getOrCreate(
                nbt -> createFromNbt(nbt, null), // Create from NBT
                FantalStateManager::new, // Create new if not present
                FantalMod.MODID
        );

        state.markDirty();
        return state;
    }

    public static PlayerFantalData getPlayerState(LivingEntity player) {
        FantalStateManager serverState = getServerState(player.getWorld().getServer());

        // uuid でプレイヤーを取得するか、プレイヤーのデータがまだない場合は、新しいプレイヤー状態を作成します
        PlayerFantalData playerState = serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerFantalData());

        return playerState;
    }

    public static void incrementFantalPollution(World world, PlayerEntity user) {
        // server stateを取得
        FantalStateManager serverState = FantalStateManager.getServerState(world.getServer());
        // server stateを更新
        serverState.totalFantalPollution += 1;

        PlayerFantalData playerState = FantalStateManager.getPlayerState(user);
        playerState.fantalPollution += 1;

        MinecraftServer server = world.getServer();

        // クライアントに送信
        PacketByteBuf data = PacketByteBufs.create();
        data.writeInt(serverState.totalFantalPollution);
        data.writeInt(playerState.fantalPollution);

        ServerPlayerEntity playerEntity = server.getPlayerManager().getPlayer(user.getUuid());
        server.execute(() -> {
            FantalMod.LOGGER.info("Sending pollution data to client");
            ServerPlayNetworking.send(playerEntity, FantalMod.FANTAL_POLLUTION, data);
        });
    }
}
