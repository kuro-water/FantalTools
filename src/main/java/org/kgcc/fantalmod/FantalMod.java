package org.kgcc.fantalmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
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
import org.kgcc.fantalmod.fantal.FantalArmorEffect;
import org.kgcc.fantalmod.registry.ModItems;
import org.kgcc.fantalmod.util.FantalStateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

import java.util.Objects;

public class FantalMod implements ModInitializer {
    // このMODのIDを取得します。
    public static final String MODID = "fantalmod";

    // このロガーはコンソールおよびログファイルにテキストを書き込むために使用されます。
    // ロガーの名前としてモッドIDを使用するのが最善の方法とされています。
    // そうすることで、どのモッドが情報、警告、エラーを書き込んだかが明確になります。
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    // 鉱石を保存するための新しいクラス レベル を作成
    public static final RegistryKey<PlacedFeature> FANTAL_ORE_PLACED_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(MODID, "fantal_ore"));

    // 汚染状態を保存するための新しいクラス レベル を作成
    public static final Identifier FANTAL_POLLUTION = new Identifier(MODID, "fantal_pollution");

    public static final int TICK_PAR_SEC = 20;

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
        GeckoLib.initialize();
        LOGGER.info("Hello Fabric world!");
//        ModItemGroup.registerItemGroups();
//        Modaitems.registerModItems();
        ModItems.initialize();

        FantalArmorEffect.register();






        // バイオームに機能を追加する 鉱石追加用
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, FANTAL_ORE_PLACED_KEY);

        // 毎tickごとにポーション効果をチェックし、かけなおす。
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
}
