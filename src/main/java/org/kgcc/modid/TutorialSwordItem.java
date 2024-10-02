package org.kgcc.modid;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.*;

public class TutorialSwordItem extends Item {
    public TutorialSwordItem() {
        super(new Item.Settings().rarity(Rarity.COMMON));
    }

    private static final String MODID = System.getProperty("modid");

    public static Item register(Item instance, String path) {
        return Registry.register(Registries.ITEM, new Identifier(MODID, path), instance);
    }
}
