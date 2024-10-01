package org.kgcc.modid;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.kgcc.modid.registry.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {
    // このMODのIDを取得します。
    public static final String MODID = System.getProperty("modid");

    // このロガーはコンソールおよびログファイルにテキストを書き込むために使用されます。
    // ロガーの名前としてモッドIDを使用するのが最善の方法とされています。
    // そうすることで、どのモッドが情報、警告、エラーを書き込んだかが明確になります。
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    // 鉱石を保存するための新しいクラス レベル を作成
    public static final RegistryKey<PlacedFeature> FANTAL_ORE_PLACED_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier("modid", "fantal_ore"));

    // 汚染状態を保存するための新しいクラス レベル を作成
    public static final Identifier FANTAL_POLLUTION = new Identifier(MODID, "fantal_pollution");

    @Override
    public void onInitialize() {
        // このコードは、Minecraftがモッドロード準備完了状態になったときに実行されます。
        // ただし、リソースなどの一部のものはまだ初期化されていない場合があります。
        // 注意して進めてください。

        LOGGER.info("Hello Fabric world!");

        ModItems.registerItems();
        TestItem.initialize();

        // バイオームに機能を追加する 鉱石追加用
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, FANTAL_ORE_PLACED_KEY);


        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            if (state.getBlock() == Blocks.GRASS_BLOCK || state.getBlock() == Blocks.DIRT) {
//                // server stateを取得
//                StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(world.getServer());
//                // server stateを更新
//                serverState.totalFantalPollution += 1;
//
//                PlayerData playerState = StateSaverAndLoader.getPlayerState(player);
//                playerState.fantalPollution += 1;
//
//                MinecraftServer server = world.getServer();
//
//                // クライアントに送信
//                PacketByteBuf data = PacketByteBufs.create();
//                data.writeInt(serverState.totalFantalPollution);
//                data.writeInt(playerState.fantalPollution);
//
//                ServerPlayerEntity playerEntity = server.getPlayerManager().getPlayer(player.getUuid());
//                server.execute(() -> {
//                    LOGGER.info("Sending pollution data to client");
//                    ServerPlayNetworking.send(playerEntity, FANTAL_POLLUTION, data);
//                });
            }
        });
    }
}
