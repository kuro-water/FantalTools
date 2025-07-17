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

public class SmeltSkill implements BaseSkill {
    // todo:浸食度とテキストメッセージの調整
    
    private final List<Tool> TOOLS = List.of(Tool.PICKAXE);
    
    @Override
    public List<Tool> getTools() {
        return TOOLS;
    }
    
    private static final HashSet<UUID> ACTIVE_PLAYERS = new HashSet<>();
    
    public static boolean isActive(PlayerEntity player) {
        return ACTIVE_PLAYERS.contains(player.getUuid());
    }
    
    @Override
    public String getTranslationKey() {
        return "smelt";
    }
    
    @Override
    public MutableText getName() {
        return Text.translatable("skill.fantalmod.smelt");
    }
    
    /**
     * <p>空中右クリック時に呼び出される。</p>
     * <p>精錬モードの切り替えを行う。</p>
     * <p>{@link org.kgcc.fantalmod.registry.OreSmeltEventHandler#register()}にてブロック破壊時動作を記述</p>
     */
    @Override
    public TypedActionResult<ItemStack> use(World world, @NotNull PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        toggleMode(user);
        // toggleModeによりACTIVE_PLAYERSにUUIDが追加・削除される
        // registry.OreSmeltingHandlerで、ACTIVE_PLAYERSを参照し、精錬を行う
        
        return TypedActionResult.success(user.getStackInHand(hand));
    }
    
    private void toggleMode(PlayerEntity player) {
        
        UUID uuid = player.getUuid();
        boolean enabled = ACTIVE_PLAYERS.contains(uuid);
        
        if (enabled) {
            ACTIVE_PLAYERS.remove(uuid);
            player.sendMessage(Text.literal("§7[スキル] 精錬モード §c無効"), false);
        } else {
            ACTIVE_PLAYERS.add(uuid);
            player.sendMessage(Text.literal("§7[スキル] 精錬モード §a有効"), false);
        }
    }
}
