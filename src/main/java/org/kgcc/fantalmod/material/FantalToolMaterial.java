package org.kgcc.fantalmod.material;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import org.kgcc.fantalmod.registry.ModItems;

public class FantalToolMaterial implements ToolMaterial {
    @Override
    //耐久値
    public int getDurability() {
        return 1800;
    }

    @Override
    //採掘速度
    public float getMiningSpeedMultiplier() {
        return 8.0f;
    }

    @Override
    //攻撃力
    public float getAttackDamage() {
        return 3.0f;
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

    // クリエイティブタブへの追加
    public static void registerCreativeTab() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.addAfter(Items.DIAMOND_HOE, ModItems.FANTAL_SHOVEL);
            entries.addAfter(ModItems.FANTAL_SHOVEL, ModItems.FANTAL_PICKAXE);
            entries.addAfter(ModItems.FANTAL_PICKAXE, ModItems.FANTAL_AXE);
            entries.addAfter(ModItems.FANTAL_AXE, ModItems.FANTAL_HOE);
            // 他のツールがあれば、ここに追加する
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.addAfter(Items.RAW_GOLD, ModItems.ROW_FANTAL);
            entries.addAfter(Items.GOLD_INGOT, ModItems.FANTAL_INGOT);
            // 他に材料系アイテムがあれば、ここに追加する
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries -> {
            entries.addAfter(Items.GOLD_ORE, ModItems.FANTAL_ORE);
//            entries.addAfter(Items.DEEPSLATE_GOLD_ORE, ModItems.DEEP_FANTAL_ORE);
            // 他の鉱石があれば、ここに追加する
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.addAfter(Items.DIAMOND_BLOCK, ModItems.FANTAL_BLOCK);
            // 他のブロックがあれば、ここに追加する
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.addAfter(Items.DIAMOND_SWORD, ModItems.FANTAL_SWORD);
            entries.addAfter(Items.DIAMOND_AXE, ModItems.FANTAL_AXE);
            entries.addAfter(Items.DIAMOND_BOOTS, ModItems.FANTAL_HELMET);
            entries.addAfter(ModItems.FANTAL_HELMET, ModItems.FANTAL_CHESTPLATE);
            entries.addAfter(ModItems.FANTAL_CHESTPLATE, ModItems.FANTAL_LEGGINGS);
            entries.addAfter(ModItems.FANTAL_LEGGINGS, ModItems.FANTAL_BOOTS);
            // 他の防具があれば、ここに追加する
        });
    }
}
