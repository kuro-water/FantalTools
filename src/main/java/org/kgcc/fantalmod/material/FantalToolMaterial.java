package org.kgcc.fantalmod.material;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import org.kgcc.fantalmod.registry.ModItems;

public class FantalToolMaterial implements ToolMaterial {
    public static void setMiningSpeed(float miningSpeedMultiplier) {
    }

    public static void setMiningLevel(int miningLevel) {
    }

    @Override
    //耐久値
    public int getDurability() {
        return 1800;
    }

    @Override
    //採掘速度
    public float getMiningSpeedMultiplier() {
        return 6.0f;
    }

    @Override
    //攻撃力
    public float getAttackDamage() {
        return 0.0f;
    }

    @Override
    //マイニングレベル(どこまでの素材を採掘できるか)
    public int getMiningLevel() {
        return 3;
    }

    @Override
    //エンチャント適性
    public int getEnchantability() {
        return 15;
    }

    @Override
    //修理の素材
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(ModItems.FANTAL_INGOT);
    }
}
