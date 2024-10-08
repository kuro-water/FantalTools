package org.kgcc.modid.test;

import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.kgcc.modid.material.FantalArmorMaterial;
import org.kgcc.modid.material.FantalToolMaterial;

import static org.kgcc.modid.FantalMod.MODID;

public class TestItem extends Item {
    private TestItem(Settings settings) {
        super(settings);
    }

    public static FantalToolMaterial TUTORIAL_TOOL_MATERIAL = new FantalToolMaterial();
    public static FantalArmorMaterial TUTORIAL_ARMOR_MATERIAL = new FantalArmorMaterial();

    // an instance of our new item
    public static final Item testItem = register(new Item(new Item.Settings()), "test_item");
    public static final Item animeItem = register(new Item(new Item.Settings()), "anime_item");

    //public static final Item tutorialSward = register(new SwordItem(TUTORIAL_TOOL_MATERIAL,7,0.6f, new Item.Settings()),"tutorial_sword");
    //public static final Item tutorialAxe = register(new AxeItem(TUTORIAL_TOOL_MATERIAL,9,-0.1f, new Item.Settings()),"tutorial_axe");
    //public static final Item tutorialPickaxe = register(new PickaxeItem(TUTORIAL_TOOL_MATERIAL,5,-2.8f, new Item.Settings()),"tutorial_pickaxe");
    //public static final Item tutorialShovel = register(new ShovelItem(TUTORIAL_TOOL_MATERIAL,5.5f,0.0f, new Item.Settings()),"tutorial_shovel");
    //public static final Item tutorialHoe = register(new HoeItem(TUTORIAL_TOOL_MATERIAL,1,4.0f, new Item.Settings()),"tutorial_hoe");

    //public static  Item tutorialHelmet = register(new ArmorItem(TUTORIAL_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Settings()),"tutorial_helmet");
    //public static  Item tutorialChestPlate = register(new ArmorItem(TUTORIAL_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings()),"tutorial_chestplate");
    //public static  Item tutorialLeggings = register(new ArmorItem(TUTORIAL_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings()),"tutorial_leggings");
    //public static  Item tutorialBoots = register(new ArmorItem(TUTORIAL_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Settings()),"tutorial_boots");

    public static Item register(Item instance, String path) {
        return Registry.register(Registries.ITEM, new Identifier(MODID, path), instance);
    }

    public static void initialize() {
    }
}
