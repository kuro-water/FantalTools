package org.kgcc.fantalmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.kgcc.fantalmod.armor.FantalArmorEffect;
import org.kgcc.fantalmod.command.SkillArgumentType;
import org.kgcc.fantalmod.init.ModEntities;
import org.kgcc.fantalmod.recall.RecallDataManager;
import org.kgcc.fantalmod.registry.*;
import org.kgcc.fantalmod.skill.RecallSkill;
import org.kgcc.fantalmod.test.ModBros;
import org.kgcc.fantalmod.tool.FantalToolItem;
import org.kgcc.fantalmod.util.FantalStateManager;
import org.kgcc.fantalmod.util.ServerTickHandler;
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
    public static final Identifier RECALL_DATA = new Identifier(MODID, "recall_data");
    
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
        FantalModCommands.registerCommands();
        
        FantalModSkills.initialize();
        
        //====追加
        OreSmeltEventHandler.register();
        AreaBreakEventHandler.register();
        //===
        
        FantalBlockEntities.registerBlockEntities();
        ModBros.registerBlocks();
        // バイオームに機能を追加する 鉱石追加用
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                FANTAL_ORE_PLACED_KEY);
        
        ServerTickHandler.register();
        RecallDataManager.register();
        RecallSkill.register();
        FantalModScreenHandlers.initialize();
        
        // todo:わんちゃんいらない説ある
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            for (ItemStack stack : handler.getPlayer().getInventory().main) {
                if (stack.getItem() instanceof FantalToolItem fantalToolItem) {
                    String translationKey = FantalToolItem.readNbt(stack);
                    fantalToolItem.setSkill(stack, translationKey);
                }
            }
            handler.getPlayer().playerScreenHandler.syncState();
        });
        
        ArgumentTypeRegistry.registerArgumentType(
                new Identifier("fantalmod", "skill"),
                SkillArgumentType.class,
                ConstantArgumentSerializer.of(SkillArgumentType::skill));
        ModEntities.register();
        
        FantalModStatusEffects.registerEffects();
    }
}
