package org.kgcc.fantalmod.item;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.fantal.FantalArmorMaterials;

public class Modaitems {
    //youtube
    public static final Item CITRINE = registerItem("citrine",
            new Item(new FabricItemSettings()));
    public static final Item RAW_CITRINE = registerItem("raw_citrine",
            new Item(new FabricItemSettings()));

//youtubeの人の
    public static final Item AMETHYST_HELMET = registerItem("amethyst_helmet",
            new AmethystArmorItem(ModArmorMaterials.AMETHYST, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item AMETHYST_CHESTPLATE = registerItem("amethyst_chestplate",
            new AmethystArmorItem(ModArmorMaterials.AMETHYST, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item AMETHYST_LEGGINGS = registerItem("amethyst_leggings",
            new AmethystArmorItem(ModArmorMaterials.AMETHYST, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item AMETHYST_BOOTS = registerItem("amethyst_boots",
            new AmethystArmorItem(ModArmorMaterials.AMETHYST, ArmorItem.Type.BOOTS, new FabricItemSettings()));
//ファンタル装備
public static final Item FANTAL_HELMET = registerItem("fantal_helmet",
        new AmethystArmorItem(FantalArmorMaterials.FANTAL, ArmorItem.Type.HELMET, new FabricItemSettings()));

public static final Item FANTAL_CHESTPLATE = registerItem("fantal_chestplate",
        new AmethystArmorItem(FantalArmorMaterials.FANTAL, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
public static final Item FANTAL_LEGGINGS = registerItem("fantal_leggings",
        new AmethystArmorItem(FantalArmorMaterials.FANTAL, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
public static final Item FANTAL_BOOTS= registerItem("fantal_boots",
    new AmethystArmorItem(FantalArmorMaterials.FANTAL, ArmorItem.Type.BOOTS, new FabricItemSettings()));





    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(FantalMod.MODID, name), item);
    }

    public static void addItemsToItemGroup() {
        addToItemGroup(ItemGroups.INGREDIENTS, CITRINE);
        addToItemGroup(ItemGroups.INGREDIENTS, RAW_CITRINE);

        addToItemGroup(ModItemGroup.CITRINE, CITRINE);
        addToItemGroup(ModItemGroup.CITRINE, RAW_CITRINE);


        addToItemGroup(ModItemGroup.CITRINE, AMETHYST_BOOTS);
        addToItemGroup(ModItemGroup.CITRINE, AMETHYST_LEGGINGS);
        addToItemGroup(ModItemGroup.CITRINE, AMETHYST_CHESTPLATE);
        addToItemGroup(ModItemGroup.CITRINE, AMETHYST_HELMET);

        addToItemGroup(ModItemGroup.CITRINE, FANTAL_BOOTS);
        addToItemGroup(ModItemGroup.CITRINE, FANTAL_LEGGINGS);
        addToItemGroup(ModItemGroup.CITRINE, FANTAL_CHESTPLATE);
        addToItemGroup(ModItemGroup.CITRINE, FANTAL_HELMET);
    }

    private static void addToItemGroup(ItemGroup group, Item item) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item));
    }
    public static void registerModItems() {
        FantalMod.LOGGER.info("Registering Mod Items for " + FantalMod.MODID);

        addItemsToItemGroup();
    }
}