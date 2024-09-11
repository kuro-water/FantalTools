package org.kgcc.modid;

import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Properties;

public class TestItem extends Item {
    private TestItem(Settings settings) {
        super(settings);
    }

    private static final String MODID = System.getProperty("modid");

    public static TutorialToolMaterial TUTORIAL_TOOL_MATERIAL = new TutorialToolMaterial();
    public static TutorialArmorMaterial TUTORIAL_ARMOR_MATERIAL = new TutorialArmorMaterial();

    // an instance of our new item
    public static final Item testItem = register(new Item(new Item.Settings()), "test_item");

    //   public static final Item tutorialSward = register(new SwordItem(TUTORIAL_TOOL_MATERIAL,7,0.6f, new Item.Settings()),"tutorial_sword");
    //   public static final Item tutorialAxe = register(new AxeItem(TUTORIAL_TOOL_MATERIAL,9,-0.1f, new Item.Settings()),"tutorial_axe");
    public static final Item tutorialPickaxe = register(new PickaxeItem(TUTORIAL_TOOL_MATERIAL,5,-2.8f, new Item.Settings()),"tutorial_pickaxe");
    //   public static final Item tutorialShovel = register(new ShovelItem(TUTORIAL_TOOL_MATERIAL,5.5,0.0f, new Item.Settings()),"tutorial_shovel");
    //   public static final Item tutorialHoe = register(new HoeItem(TUTORIAL_TOOL_MATERIAL,1,4.0f, new Item.Settings()),"tutorial_hoe");

    public static  Item tutorialHelmet = register(new ArmorItem(TUTORIAL_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Settings()),"tutorial_helmet");
    public static  Item tutorialChestPlate = register(new ArmorItem(TUTORIAL_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings()),"tutorial_chestplate");
    public static  Item tutorialLeggings = register(new ArmorItem(TUTORIAL_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings()),"tutorial_leggings");
    public static  Item tutorialBoots = register(new ArmorItem(TUTORIAL_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Settings()),"tutorial_boots");

    public static Item register(Item instance, String path) {
        return Registry.register(Registries.ITEM, new Identifier(MODID, path), instance);
    }

    public static void initialize() {
    }
}
