package org.kgcc.fantalmod.skill;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.kgcc.fantalmod.util.FantalStateManager;

import java.util.List;

public class StrengthSkill implements BaseSkill {
    private final List<Tool> TOOLS = List.of(Tool.PICKAXE, Tool.AXE, Tool.SHOVEL, Tool.HOE, Tool.SWORD);
    
    @Override
    public List<Tool> getTools() {
        return TOOLS;
    }
    
    @Override
    public String getTranslationKey() {
        return "strength";
    }
    
    @Override
    public MutableText getName() {
        return Text.translatable("skill.fantalmod.strength");
    }
    
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        
        user.addStatusEffect(
                new StatusEffectInstance(
                        StatusEffects.STRENGTH,
                        20 * FantalStateManager.TICK_PAR_SEC,
                        1));
        
        var server = world.getServer();
        FantalStateManager.addFantalPollution(server, user, 1);
        
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
