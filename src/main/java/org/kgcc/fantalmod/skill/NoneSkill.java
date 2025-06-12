package org.kgcc.fantalmod.skill;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class NoneSkill implements BaseSkill {
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
