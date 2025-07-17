package org.kgcc.fantalmod.skill.PickaxeSkill;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.kgcc.fantalmod.skill.BaseSkill;
import org.kgcc.fantalmod.skill.Tool;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class HammerSkill implements BaseSkill {
    // todo:浸食度とテキストメッセージの調整
    // todo:overlay = trueにしよう
    // todo:translationKeyの修正
    
    private final List<Tool> TOOLS = List.of(Tool.PICKAXE);
    
    @Override
    public List<Tool> getTools() {
        return TOOLS;
    }
    
    @Override
    public String getTranslationKey() {
        return "hammer";
    }
    
    @Override
    public MutableText getName() {
        return Text.translatable("skill.fantalmod.hammer");
    }
    
    private static final HashSet<UUID> ACTIVE_PLAYERS = new HashSet<>();
    
    public static boolean isActive(PlayerEntity player) {
        return ACTIVE_PLAYERS.contains(player.getUuid());
    }
    
    /**
     * <p>空中右クリック時に呼び出される。</p>
     * <p>範囲破壊モードの切り替えを行う。</p>
     * <p>{@link org.kgcc.fantalmod.registry.AreaBreakEventHandler#register()}にてブロック破壊時動作を記述</p>
     */
    @Override
    public TypedActionResult<ItemStack> use(World world, @NotNull PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        // 空中を右クリックしたときだけ切り替え
        toggleMode(user);
        // toggleModeによりACTIVE_PLAYERSにUUIDが追加・削除される
        // registry.AreaBreakEventHandlerで、ACTIVE_PLAYERSを参照し、範囲破壊を行う
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
}
