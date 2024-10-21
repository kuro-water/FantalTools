package org.kgcc.fantalmod.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.block.FantalBlock;
import org.kgcc.fantalmod.block.FantalOreBlock;
import org.kgcc.fantalmod.fantal.FantalArmorMaterials;
import org.kgcc.fantalmod.item.*;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;

import static org.kgcc.fantalmod.FantalMod.MODID;

public class ModItems {
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

    //public static final Item FANTAL_HELMET = registerItem(new FantalHelmetItem(), "fantal_helmet");
    //public static final Item FANTAL_CHESTPLATE = registerItem(new FantalChestplateItem(), "fantal_chestplate");
    //public static final Item FANTAL_LEGGINGS = registerItem(new FantalLeggingsItem(), "fantal_leggings");
    //public static final Item FANTAL_BOOTS = registerItem(new FantalBootsItem(), "fantal_boots");

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

    public static void initialize() {
        FantalMod.LOGGER.info("ModItems registered.");
    }
}
