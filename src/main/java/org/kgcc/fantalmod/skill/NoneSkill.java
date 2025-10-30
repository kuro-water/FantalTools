package org.kgcc.fantalmod.skill;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

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
}
