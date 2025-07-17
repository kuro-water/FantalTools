package org.kgcc.fantalmod.skill;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class NoneSkill implements BaseSkill {
    private final List<Tool> TOOLS = List.of(Tool.PICKAXE, Tool.AXE, Tool.SHOVEL, Tool.HOE, Tool.SWORD);
    
    @Override
    public List<Tool> getTools() {
        return TOOLS;
    }
    
    @Override
    public String getTranslationKey() {
        return "none";
    }
    
    @Override
    public MutableText getName() {
        return Text.translatable("skill.fantalmod.none");
    }
    
    @Override
    public void appendTooltip(ItemStack stack, World world, java.util.List<Text> tooltip, TooltipContext context) {
    }
}
