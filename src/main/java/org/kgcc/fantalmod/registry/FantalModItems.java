package org.kgcc.fantalmod.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.armor.FantalArmorItem;
import org.kgcc.fantalmod.armor.FantalArmorMaterials;
import org.kgcc.fantalmod.block.FantalBlock;
import org.kgcc.fantalmod.block.FantalOreBlock;
import org.kgcc.fantalmod.item.FantalIngotItem;
import org.kgcc.fantalmod.item.FantalNuggetItem;
import org.kgcc.fantalmod.item.RowFantalItem;
import org.kgcc.fantalmod.tool.*;

import static org.kgcc.fantalmod.FantalMod.MODID;

public class FantalModItems {
    public static final Item FANTAL_INGOT = registerItem(new FantalIngotItem(), "fantal_ingot");
    public static final Item ROW_FANTAL = registerItem(new RowFantalItem(), "row_fantal");
    public static final Item FANTAL_NUGGET = registerItem(new FantalNuggetItem(), "fantal_nugget");

    public static final Block FANTAL_ORE = registerBlock(new FantalOreBlock(), "fantal_ore");
    public static final Block DEEP_FANTAL_ORE = registerBlock(new FantalOreBlock(), "deepslate_fantal_ore");
    public static final Block FANTAL_BLOCK = registerBlock(new FantalBlock(), "fantal_block");

    public static final Item FANTAL_SWORD = registerItem(new FantalSwordItem(), "fantal_sword");
    public static final Item FANTAL_AXE = registerItem(new FantalAxeItem(), "fantal_axe");
    public static final Item FANTAL_PICKAXE = registerItem(new FantalPickaxeItem(), "fantal_pickaxe");
    public static final Item FANTAL_SHOVEL = registerItem(new FantalShovelItem(), "fantal_shovel");
    public static final Item FANTAL_HOE = registerItem(new FantalHoeItem(), "fantal_hoe");

    public static final Item FANTAL_HELMET =
            registerItem(new FantalArmorItem(FantalArmorMaterials.FANTAL, ArmorItem.Type.HELMET, new FabricItemSettings()), "fantal_helmet");
    public static final Item FANTAL_CHESTPLATE =
            registerItem(new FantalArmorItem(FantalArmorMaterials.FANTAL, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()), "fantal_chestplate");
    public static final Item FANTAL_LEGGINGS =
            registerItem(new FantalArmorItem(FantalArmorMaterials.FANTAL, ArmorItem.Type.LEGGINGS, new FabricItemSettings()), "fantal_leggings");
    public static final Item FANTAL_BOOTS =
            registerItem(new FantalArmorItem(FantalArmorMaterials.FANTAL, ArmorItem.Type.BOOTS, new FabricItemSettings()), "fantal_boots");

    public static Item registerItem(Item instance, String path) {
        return Registry.register(Registries.ITEM, new Identifier(MODID, path), instance);
    }

    public static Block registerBlock(Block block, String path) {
        Registry.register(Registries.ITEM, new Identifier(MODID, path), new BlockItem(block, new Item.Settings()));
        return Registry.register(Registries.BLOCK, new Identifier(MODID, path), block);
    }

    // クリエイティブタブへの追加
    public static void registerCreativeTab() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.addAfter(Items.DIAMOND_HOE, FantalModItems.FANTAL_SHOVEL);
            entries.addAfter(FantalModItems.FANTAL_SHOVEL, FantalModItems.FANTAL_PICKAXE);
            entries.addAfter(FantalModItems.FANTAL_PICKAXE, FantalModItems.FANTAL_AXE);
            entries.addAfter(FantalModItems.FANTAL_AXE, FantalModItems.FANTAL_HOE);
            // 他のツールがあれば、ここに追加する
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.addAfter(Items.RAW_GOLD, FantalModItems.ROW_FANTAL);
            entries.addAfter(Items.GOLD_INGOT, FantalModItems.FANTAL_INGOT);
            // 他に材料系アイテムがあれば、ここに追加する
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries -> {
            entries.addAfter(Items.DIAMOND_ORE, FantalModItems.FANTAL_ORE);
            entries.addAfter(FantalModItems.FANTAL_ORE, FantalModItems.DEEP_FANTAL_ORE);
            // 他の鉱石があれば、ここに追加する
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.addAfter(Items.DIAMOND_BLOCK, FantalModItems.FANTAL_BLOCK);
            // 他のブロックがあれば、ここに追加する
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.addAfter(Items.DIAMOND_SWORD, FantalModItems.FANTAL_SWORD);
            entries.addAfter(Items.DIAMOND_AXE, FantalModItems.FANTAL_AXE);
            entries.addAfter(Items.DIAMOND_BOOTS, FantalModItems.FANTAL_HELMET);
            entries.addAfter(FantalModItems.FANTAL_HELMET, FantalModItems.FANTAL_CHESTPLATE);
            entries.addAfter(FantalModItems.FANTAL_CHESTPLATE, FantalModItems.FANTAL_LEGGINGS);
            entries.addAfter(FantalModItems.FANTAL_LEGGINGS, FantalModItems.FANTAL_BOOTS);
            // 他の防具があれば、ここに追加する
        });
    }

    public static void initialize() {
        FantalMod.LOGGER.info("ModItems registered.");
    }
}
