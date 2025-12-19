package org.kgcc.fantalmod.registry;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import org.kgcc.fantalmod.command.SkillArgumentType;
import org.kgcc.fantalmod.skill.BaseSkill;
import org.kgcc.fantalmod.tool.FantalToolItem;
import org.kgcc.fantalmod.util.FantalStateManager;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FantalModCommands {
    
    public static void notifyAllPlayers(MinecraftServer server, String message) {
        // /gamerule sendCommandFeedback を確認
        if (!server.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
            return;
        }
        
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.sendMessage(Text.literal(message), false);
        }
    }
    
    public static void registerCommands() {
        // 実行者の汚染度を変更するコマンド
        var addFantalPollution = literal("add").then(argument("value", IntegerArgumentType.integer()).requires(
                source -> source.hasPermissionLevel(2)).executes(context -> {
            final int value = IntegerArgumentType.getInteger(context, "value");
            
            var player = context.getSource().getPlayer();
            if (player == null) {
                return 0;
            }
            
            FantalStateManager.addFantalPollution(player.world.getServer(), player, value);
            
            var playerState = FantalStateManager.getPlayerState(player);
            notifyAllPlayers(
                    Objects.requireNonNull(player.world.getServer()),
                    "%sが%sの侵食度を%sに設定しました".formatted(
                            context.getSource().getName(),
                            player.getName().getString(),
                            playerState.getFantalPollution()));
            return 1;
        }));
        
        // 指定したプレイヤーの汚染度を変更するコマンド
        var addFantalPollutionPlayers = literal("add").then(argument("players", EntityArgumentType.players()).requires(
                source -> source.hasPermissionLevel(2)).then(argument("value", IntegerArgumentType.integer()).requires(
                source -> source.hasPermissionLevel(2)).executes(context -> {
            // playersにはコマンド実行時の引数のプレイヤーが入る
            final var players = EntityArgumentType.getPlayers(context, "players");
            final int value = IntegerArgumentType.getInteger(context, "value");
            
            for (PlayerEntity player : players) {
                FantalStateManager.addFantalPollution(player.world.getServer(), player, value);
                var playerState = FantalStateManager.getPlayerState(player);
                notifyAllPlayers(
                        Objects.requireNonNull(player.world.getServer()),
                        "%sが%sの侵食度を%sに設定しました".formatted(
                                context.getSource().getName(),
                                player.getName().getString(),
                                playerState.getFantalPollution()));
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
            notifyAllPlayers(
                    Objects.requireNonNull(player.world.getServer()),
                    "%sが%sの侵食度を%sに設定しました".formatted(
                            context.getSource().getName(),
                            player.getName().getString(),
                            playerState.getFantalPollution()));
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
                notifyAllPlayers(
                        Objects.requireNonNull(player.world.getServer()),
                        "%sが%sの侵食度を%sに設定しました".formatted(
                                context.getSource().getName(),
                                player.getName().getString(),
                                playerState.getFantalPollution()));
            }
            return 1;
        })));
        
        var showFantalPollution = literal("show").executes(context -> {
            var player = context.getSource().getPlayer();
            if (player == null) {
                return 0;
            }
            
            var playerState = FantalStateManager.getPlayerState(player);
            notifyAllPlayers(
                    Objects.requireNonNull(player.world.getServer()),
                    "%sの侵食度： %s".formatted(
                            player.getName().getString(),
                            playerState.getFantalPollution()));
            return 1;
        });
        
        var showFantalPollutionPlayers = literal("show").then(
                argument("players", EntityArgumentType.players()).executes(context -> {
                    final var players = EntityArgumentType.getPlayers(context, "players");
                    
                    for (PlayerEntity player : players) {
                        final var playerState = FantalStateManager.getPlayerState(player);
                        notifyAllPlayers(
                                Objects.requireNonNull(player.world.getServer()),
                                "%sの侵食度： %s".formatted(
                                        player.getName().getString(),
                                        playerState.getFantalPollution()));
                    }
                    return 1;
                }));
        
        var setSkill = literal("setskill").then(argument("skill", SkillArgumentType.skill()).requires(
                source -> source.hasPermissionLevel(2)).executes(context -> {
//            FantalMod.LOGGER.info("start");
            BaseSkill skill;
            try {
                skill = SkillArgumentType.getSkill(context, "skill");
//                FantalMod.LOGGER.info("Setting skill: {}", skill.getName().getString());
            } catch (Exception e) {
                context.getSource().sendError(Text.translatable("argument.fantalmod.skill.invalid"));
//                FantalMod.LOGGER.error(e.getLocalizedMessage());
                return 0;
            }
            
            var player = context.getSource().getPlayer();
            if (player == null) {
                return 0;
            }
            
            var itemStack = player.getMainHandStack();
            if (itemStack.isEmpty()) {
//                FantalMod.LOGGER.info("Player {} has no item in hand", player.getName().getString());
                player.sendMessage(Text.translatable("command.fantalmod.skill.give.failure.no_item"), false);
                return -1;
            }
            
            if (itemStack.getItem() instanceof FantalToolItem fantalToolItem) {
                fantalToolItem.setSkill(itemStack, skill);
            } else {
                var itemName = itemStack.getName().getString();
//                FantalMod.LOGGER.info("Item {} is not a FantalToolItem", itemName);
                var text = Text.translatable("command.fantalmod.skill.give.failure.not_fantal_item", itemName);
                player.sendMessage(text, false);
                return -1;
            }
            
            var server = Objects.requireNonNull(player.world.getServer());
            var skillName = skill.getName().getString();
            var text = Text.translatable("command.fantalmod.skill.give.success", skillName);
            notifyAllPlayers(server, text.getString());
            
            return 0;
        }));
        
        
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("fantalmod").then(addFantalPollution)
                                                    .then(addFantalPollutionPlayers)
                                                    .then(setFantalPollution)
                                                    .then(setFantalPollutionPlayers)
                                                    .then(showFantalPollution)
                                                    .then(showFantalPollutionPlayers)
                                                    .then(setSkill));
        });
    }
}
