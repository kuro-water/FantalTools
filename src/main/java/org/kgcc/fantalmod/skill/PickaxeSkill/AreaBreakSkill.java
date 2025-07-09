package org.kgcc.fantalmod.skill.PickaxeSkill;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.kgcc.fantalmod.skill.BaseSkill;

import java.util.HashSet;
import java.util.UUID;

public class AreaBreakSkill implements BaseSkill {
    // todo:浸食度とテキストメッセージの調整
    private static final HashSet<UUID> ACTIVE_PLAYERS = new HashSet<>();

    @Override
    public String getTranslationKey() {
        return "AreaBreakSkill";
    }

    @Override
    public MutableText getName() {
        return Text.translatable("skill.fantalmod.area_break");
    }



    // 空中右クリック時
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user == null || world.isClient()) return TypedActionResult.pass(user.getStackInHand(hand));
        // 空中を右クリックしたときだけ切り替え
        toggleMode(user);
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    private void toggleMode(PlayerEntity player) {
        UUID uuid = player.getUuid();
        boolean enabled = ACTIVE_PLAYERS.contains(uuid);

        if (enabled) {
            ACTIVE_PLAYERS.remove(uuid);
            player.sendMessage(Text.literal("§7[スキル] 範囲破壊モード §c無効"), false);
        } else {
            ACTIVE_PLAYERS.add(uuid);
            player.sendMessage(Text.literal("§7[スキル] 範囲破壊モード §a有効"), false);
        }
    }

    public static boolean isActive(PlayerEntity player) {
        return ACTIVE_PLAYERS.contains(player.getUuid());
    }
}
