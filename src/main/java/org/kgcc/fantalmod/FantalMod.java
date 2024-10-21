package org.kgcc.fantalmod;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.kgcc.fantalmod.armor.FantalArmorEffect;
import org.kgcc.fantalmod.registry.ModItems;
import org.kgcc.fantalmod.util.FantalStateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

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
            var hasteDuration = Objects.requireNonNull(player.getStatusEffect(effect)).getDuration();
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

        ModItems.initialize();
        ModItems.registerCreativeTab();
        FantalArmorEffect.register();
        FantalStateManager.register();

        // バイオームに機能を追加する 鉱石追加用
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, FANTAL_ORE_PLACED_KEY);


        var addFantalPollutionArgument =
                argument("value", IntegerArgumentType.integer()).requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            final int value = IntegerArgumentType.getInteger(context, "value");
                            var player = context.getSource().getPlayer();
                            if (player == null) {
                                return 0;
                            }
                            FantalStateManager.addFantalPollution(player.world, player, value);
                            var playerState = FantalStateManager.getPlayerState(player);
                            context.getSource()
                                    .sendFeedback(Text.literal("%s's Pollution: %s".formatted(player.getUuid(), playerState.fantalPollution)), false);
                            return 1;
                        });
        var addFantalPollutionLiteral = literal("addFantalPollution").then(addFantalPollutionArgument);

        var addFantalPollutionPlayerArgument =
                argument("player", EntityArgumentType.player()).requires(source -> source.hasPermissionLevel(2))
                        .then(argument("value", IntegerArgumentType.integer()).requires(source -> source.hasPermissionLevel(2))
                                      .executes(context -> {
                                          final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                          final int value = IntegerArgumentType.getInteger(context, "value");
                                          FantalStateManager.addFantalPollution(player.world, player, value);
                                          var playerState = FantalStateManager.getPlayerState(player);
                                          context.getSource()
                                                  .sendFeedback(Text.literal("%s's Pollution: %s".formatted(player.getUuid(), playerState.fantalPollution)), false);
                                          return 1;
                                      }));
        var addFantalPollutionPlayerLiteral = literal("addFantalPollution").then(addFantalPollutionPlayerArgument);

        var setFantalPollutionArgument =
                argument("value", IntegerArgumentType.integer()).requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            final int value = IntegerArgumentType.getInteger(context, "value");
                            var player = context.getSource().getPlayer();
                            if (player == null) {
                                return 0;
                            }
                            FantalStateManager.setFantalPollution(player.world, player, value);
                            var playerState = FantalStateManager.getPlayerState(player);
                            context.getSource()
                                    .sendFeedback(Text.literal("%s's Pollution: %s".formatted(player.getUuid(), playerState.fantalPollution)), false);
                            return 1;
                        });
        var setFantalPollutionLiteral = literal("setFantalPollution").then(setFantalPollutionArgument);

        var setFantalPollutionPlayerArgument =
                argument("player", EntityArgumentType.player()).requires(source -> source.hasPermissionLevel(2))
                        .then(argument("value", IntegerArgumentType.integer()).requires(source -> source.hasPermissionLevel(2))
                                      .executes(context -> {
                                          final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                          final int value = IntegerArgumentType.getInteger(context, "value");
                                          FantalStateManager.setFantalPollution(player.world, player, value);
                                          var playerState = FantalStateManager.getPlayerState(player);
                                          context.getSource()
                                                  .sendFeedback(Text.literal("%s's Pollution: %s".formatted(player.getUuid(), playerState.fantalPollution)), false);
                                          return 1;
                                      }));
        var setFantalPollutionPlayerLiteral = literal("setFantalPollution").then(setFantalPollutionPlayerArgument);

        var showFantalPollutionLiteral = literal("showFantalPollution").executes(context -> {
            var player = context.getSource().getPlayer();
            if (player == null) {
                return 0;
            }
            var playerState = FantalStateManager.getPlayerState(player);
            context.getSource()
                    .sendFeedback(Text.literal("%s's Pollution: %s".formatted(player.getUuid(), playerState.fantalPollution)), false);
            return 1;
        });

        var showFantalPollutionPlayerLiteral =
                literal("showFantalPollution").then(argument("player", EntityArgumentType.player()).executes(context -> {
                    final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                    final var playerState = FantalStateManager.getPlayerState(player);
                    context.getSource()
                            .sendFeedback(Text.literal("%s's Pollution: %s".formatted(player.getUuid(), playerState.fantalPollution)), false);
                    return 1;
                }));

        var humei = literal("foo").executes(context -> {
            context.getSource().sendFeedback(Text.literal("Called foo without bar"), false);
            return 1;
        }).then(literal("bar").executes(context -> {
            context.getSource().sendFeedback(Text.literal("Called foo with bar"), false);
            return 1;
        })).then(literal("baz").executes(context -> {
            context.getSource().sendFeedback(Text.literal("Called foo with baz"), false);
            return 1;
        })).then(literal("baz").then(argument("value", EntityArgumentType.player()).executes(context -> {
            final PlayerEntity value = EntityArgumentType.getPlayer(context, "value");
            context.getSource().sendFeedback(Text.literal("Called foo with baz and value: %s".formatted(value)), false);
            return 1;
        })));

        // コマンドを登録
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(humei));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("fantalmod").then(addFantalPollutionLiteral)
                                    .then(addFantalPollutionPlayerLiteral)
                                    .then(setFantalPollutionLiteral)
                                    .then(setFantalPollutionPlayerLiteral)
                                    .then(showFantalPollutionLiteral)
                                    .then(showFantalPollutionPlayerLiteral)));
    }
}
