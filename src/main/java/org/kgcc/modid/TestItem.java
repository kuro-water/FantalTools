package org.kgcc.modid;

import net.minecraft.item.Item;
import net.minecraft.item.PickaxeItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class TestItem extends Item {
    private TestItem(Settings settings) {
        super(settings);
    }

    private static final String MODID = System.getProperty("modid");

    public static TutorialToolMaterial TUTORIAL_TOOL_MATERIAL = new TutorialToolMaterial();

    // an instance of our new item
    public static final Item testItem = register(new Item(new Item.Settings()), "test_item");

    public static final Item tutorialPickaxe = register(new PickaxeItem(TUTORIAL_TOOL_MATERIAL,1,-1.0f, new Item.Settings()),"tutorial_pickaxe");

    public static Item register(Item instance, String path) {
        return Registry.register(Registries.ITEM, new Identifier(MODID, path), instance);
    }

    public static void initialize() {
    }
}
