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
        var addFantalPollution = literal("add").then(argument("value", IntegerArgumentType.integer()).requires(
                source -> source.hasPermissionLevel(2)).executes(context -> {
            final int value = IntegerArgumentType.getInteger(context, "value");
            var player = context.getSource().getPlayer();
            if (player == null) {
                return 0;
            }
            FantalStateManager.addFantalPollution(player.world, player, value);
            var playerState = FantalStateManager.getPlayerState(player);
            context.getSource().sendFeedback(Text.literal(
                    "%sの侵食度： %s".formatted(player.getName().getString(), playerState.fantalPollution)), false);
            return 1;
        }));
        
        var addFantalPollutionPlayers = literal("add").then(argument("players", EntityArgumentType.players()).requires(
                source -> source.hasPermissionLevel(2)).then(argument("value", IntegerArgumentType.integer()).requires(
                source -> source.hasPermissionLevel(2)).executes(context -> {
            final var players = EntityArgumentType.getPlayers(context, "players");
            final int value = IntegerArgumentType.getInteger(context, "value");
            for (PlayerEntity player : players) {
                FantalStateManager.addFantalPollution(player.world, player, value);
                var playerState = FantalStateManager.getPlayerState(player);
                context.getSource().sendFeedback(Text.literal(
                                                         "%sの侵食度： %s".formatted(player.getName().getString(), playerState.fantalPollution)),
                                                 false);
            }
            return 1;
        })));
        
        var setFantalPollution = literal("set").then(argument("value", IntegerArgumentType.integer()).requires(
                source -> source.hasPermissionLevel(2)).executes(context -> {
            final int value = IntegerArgumentType.getInteger(context, "value");
            var player = context.getSource().getPlayer();
            if (player == null) {
                return 0;
            }
            FantalStateManager.setFantalPollution(player, value);
            var playerState = FantalStateManager.getPlayerState(player);
            context.getSource().sendFeedback(Text.literal(
                    "%sの侵食度： %s".formatted(player.getName().getString(), playerState.fantalPollution)), false);
            return 1;
        }));
        
        var setFantalPollutionPlayers = literal("set").then(argument("players", EntityArgumentType.players()).requires(
                source -> source.hasPermissionLevel(2)).then(argument("value", IntegerArgumentType.integer()).requires(
                source -> source.hasPermissionLevel(2)).executes(context -> {
            final var players = EntityArgumentType.getPlayers(context, "players");
            final int value = IntegerArgumentType.getInteger(context, "value");
            for (PlayerEntity player : players) {
                FantalStateManager.setFantalPollution(player, value);
                var playerState = FantalStateManager.getPlayerState(player);
                context.getSource().sendFeedback(Text.literal(
                                                         "%sの侵食度： %s".formatted(player.getName().getString(), playerState.fantalPollution)),
                                                 false);
            }
            return 1;
        })));
        
        var showFantalPollution = literal("show").executes(context -> {
            var player = context.getSource().getPlayer();
            if (player == null) {
                return 0;
            }
            var playerState = FantalStateManager.getPlayerState(player);
            context.getSource().sendFeedback(Text.literal(
                    "%sの侵食度： %s".formatted(player.getName().getString(), playerState.fantalPollution)), false);
            return 1;
        });
        
        var showFantalPollutionPlayers = literal("show").then(
                argument("players", EntityArgumentType.players()).executes(context -> {
                    final var players = EntityArgumentType.getPlayers(context, "players");
                    for (PlayerEntity player : players) {
                        final var playerState = FantalStateManager.getPlayerState(player);
                        context.getSource().sendFeedback(Text.literal(
                                "%sの侵食度： %s".formatted(player.getName().getString(),
                                                           playerState.fantalPollution)), false);
                    }
                    return 1;
                }));
        
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("fantalmod").then(addFantalPollution)
                                                    .then(addFantalPollutionPlayers)
                                                    .then(setFantalPollution)
                                                    .then(setFantalPollutionPlayers)
                                                    .then(showFantalPollution)
                                                    .then(showFantalPollutionPlayers));
        });
    }
}
