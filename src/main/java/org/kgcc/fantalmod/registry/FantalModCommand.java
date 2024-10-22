package org.kgcc.fantalmod.registry;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.kgcc.fantalmod.util.FantalStateManager;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FantalModCommand {
    public static void registerCommands() {

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
                                    .sendFeedback(Text.literal("%s's Pollution: %s".formatted(player.getName(), playerState.fantalPollution)), false);
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
                                                  .sendFeedback(Text.literal("%s's Pollution: %s".formatted(player.getName(), playerState.fantalPollution)), false);
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
                                    .sendFeedback(Text.literal("%s's Pollution: %s".formatted(player.getName(), playerState.fantalPollution)), false);
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
                                                  .sendFeedback(Text.literal("%s's Pollution: %s".formatted(player.getName(), playerState.fantalPollution)), false);
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
                    .sendFeedback(Text.literal("%s's Pollution: %s".formatted(player.getName(), playerState.fantalPollution)), false);
            return 1;
        });

        var showFantalPollutionPlayerLiteral =
                literal("showFantalPollution").then(argument("player", EntityArgumentType.player()).executes(context -> {
                    final PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                    final var playerState = FantalStateManager.getPlayerState(player);
                    context.getSource()
                            .sendFeedback(Text.literal("%s's Pollution: %s".formatted(player.getName(), playerState.fantalPollution)), false);
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
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("fantalmod").then(addFantalPollutionLiteral)
                                        .then(addFantalPollutionPlayerLiteral)
                                        .then(setFantalPollutionLiteral)
                                        .then(setFantalPollutionPlayerLiteral)
                                        .then(showFantalPollutionLiteral)
                                        .then(showFantalPollutionPlayerLiteral));
        });
    }
}
