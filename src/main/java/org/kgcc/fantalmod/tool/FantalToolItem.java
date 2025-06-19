package org.kgcc.fantalmod.tool;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kgcc.fantalmod.registry.FantalModSkills;
import org.kgcc.fantalmod.skill.BaseSkill;

public interface FantalToolItem {
    String SKILL_NBT_KEY = "fantalmod.skill";
    
    BaseSkill getSkill();
    
    // todo:nbtにしないと。
    void setSkill(ItemStack stack, @NotNull BaseSkill skill);
    
    default void setSkill(ItemStack stack, @Nullable String skillName) {
        BaseSkill skill = FantalModSkills.SKILLS.stream()
                                                .filter(s -> s.getName().getString().equals(skillName))
                                                .findFirst()
                                                .orElse(FantalModSkills.NONE);
        setSkill(stack, skill);
    }
    
    static void writeNbt(ItemStack stack, String skillName) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putString(SKILL_NBT_KEY, skillName);
    }
    
    static String readNbt(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        return nbt != null && nbt.contains(SKILL_NBT_KEY) ? nbt.getString(SKILL_NBT_KEY) : "";
    }
}
