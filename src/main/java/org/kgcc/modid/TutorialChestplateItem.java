package org.kgcc.modid;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class TutorialChestplateItem extends Item {
    public TutorialChestplateItem() {
        super(new Settings().rarity(Rarity.COMMON));
    }

    private static final String MODID = System.getProperty("modid");

    public static Item register(Item instance, String path) {
        return Registry.register(Registries.ITEM, new Identifier(MODID, path), instance);
    }
}
