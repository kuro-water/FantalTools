package org.kgcc.fantalmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.kgcc.fantalmod.armor.FantalArmorEffect;
import org.kgcc.fantalmod.registry.FantalModCommand;
import org.kgcc.fantalmod.registry.FantalModStatusEffects;
import org.kgcc.fantalmod.registry.FantalModItems;
import org.kgcc.fantalmod.util.FantalStateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @Override
    public void onInitialize() {
        // このコードは、Minecraftがモッドロード準備完了状態になったときに実行されます。
        // ただし、リソースなどの一部のものはまだ初期化されていない場合があります。
        // 注意して進めてください。

        LOGGER.info("Hello Fabric world!");

        FantalModItems.initialize();
        FantalModItems.registerCreativeTab();
        FantalArmorEffect.register();
        FantalStateManager.register();
        FantalModCommand.registerCommands();
        FantalModStatusEffects.registerEffects();

        // バイオームに機能を追加する 鉱石追加用
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, FANTAL_ORE_PLACED_KEY);

    }
}
