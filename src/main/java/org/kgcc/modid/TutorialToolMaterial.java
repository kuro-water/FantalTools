package org.kgcc.modid;

import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class TutorialToolMaterial implements ToolMaterial {
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
        return Ingredient.ofItems(Items.DIRT);
    }
}
