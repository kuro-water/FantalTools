package org.kgcc.fantalmod;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class TestItem extends Item {
    private TestItem(Settings settings) {
        super(settings);
    }

    // an instance of our new item
    public static final Item testItem = register(new Item(new Item.Settings()), "test_item");
    public static final Item animeItem = register(new Item(new Item.Settings()), "anime_item");

    public static Item register(Item instance, String path) {
        return Registry.register(Registries.ITEM, new Identifier(FantalMod.MODID, path), instance);
    }

    public static void initialize() {
    }
}
