package org.kgcc.modid;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.KelpPlantBlock;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.kgcc.modid.registry.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

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

    private static final int TICK_PAR_SEC = 20;

    public static void KeepStatusEffect(PlayerEntity player, StatusEffect effect) {
        // duration（継続時間）: 20 ticks = 1 seconds
        // amplifier（強度）
        try {
            // effectの残り時間をチェック
            var hasteDuration = Objects.requireNonNull(player.getStatusEffect(effect))
                                       .getDuration();
            if (hasteDuration < 5 * TICK_PAR_SEC) {
                player.addStatusEffect(new StatusEffectInstance(effect, 10 * TICK_PAR_SEC, 0, false, false));
            }
        } catch (NullPointerException _e) {
            // effectが付与されていない場合NullPointerExceptionが発生するので、こちらで再度付与
            player.addStatusEffect(new StatusEffectInstance(effect, 10 * TICK_PAR_SEC, 0, false, false));
        }
    }

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

        // 毎tickごとにポーション効果をチェックする
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                var playerState = StateSaverAndLoader.getPlayerState(player);
                switch (playerState.fantalPollution) {
                    case 0: {
                        break;
                    }
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5: {
                        KeepStatusEffect(player, StatusEffects.SPEED);
                        break;
                    }
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10: {
                        KeepStatusEffect(player, StatusEffects.HASTE);
                        break;
                    }
                    default: {
                        KeepStatusEffect(player, StatusEffects.NAUSEA);
                        break;
                    }
                }
            }
        });
    }
}
