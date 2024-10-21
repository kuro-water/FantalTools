package org.kgcc.fantalmod.material;


import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.kgcc.fantalmod.registry.ModItems;

public class FantalArmorMaterial implements ArmorMaterial {

    //耐久値
    public int getDurability(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> 370;
            case CHESTPLATE -> 550;
            case LEGGINGS -> 525;
            case BOOTS -> 450;
        };
    }

    //防御力
    public int getProtection(ArmorItem.Type type) {
        return switch (type) {
            case BOOTS, HELMET -> 3;
            case LEGGINGS -> 6;
            case CHESTPLATE -> 8;
        };
    }

    //エンチャント適正
    @Override
    public int getEnchantability() {
        return 15;
    }

    //装着音
    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND;
    }

    //修復アイテム
    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(ModItems.FANTAL_INGOT);
    }

    //名前
    @Override
    public String getName() {
        return "fantal";
    }

    //防具強度
    @Override
    public float getToughness() {
        return 3.0f;
    }

    //ノックバック耐性
    @Override
    public float getKnockbackResistance() {
        return 0.1f;
    }
}
