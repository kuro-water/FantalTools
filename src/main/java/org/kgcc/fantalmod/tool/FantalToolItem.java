package org.kgcc.fantalmod.tool;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kgcc.fantalmod.registry.FantalModSkills;
import org.kgcc.fantalmod.skill.BaseSkill;

public interface FantalToolItem {
    String SKILL_NBT_KEY = "fantalmod.skill";
    
    default BaseSkill getSkill(ItemStack stack) {
        String translationKey = readNbt(stack);
        return FantalModSkills.SKILLS.stream()
                                     .filter(s -> s.getTranslationKey().equals(translationKey))
                                     .findFirst()
                                     .orElse(FantalModSkills.NONE);
    }
    
    default void setSkill(ItemStack stack, @NotNull BaseSkill skill) {
        writeNbt(stack, skill.getTranslationKey());
    }
    
    default void setSkill(ItemStack stack, @Nullable String translationKey) {
        writeNbt(stack, translationKey != null ? translationKey : "");
    }
    
    static void writeNbt(ItemStack stack, String translationKey) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putString(SKILL_NBT_KEY, translationKey);
    }
    
    static String readNbt(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        return nbt != null && nbt.contains(SKILL_NBT_KEY) ? nbt.getString(SKILL_NBT_KEY) : "";
    }
}
