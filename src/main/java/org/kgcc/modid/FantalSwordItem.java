package org.kgcc.modid;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.*;

import static org.kgcc.modid.ExampleMod.MODID;

public class FantalSwordItem extends Item {
    public FantalSwordItem() {
        super(new Item.Settings().rarity(Rarity.COMMON));
    }

    public static Item register(Item instance, String path) {
        return Registry.register(Registries.ITEM, new Identifier(MODID, path), instance);
    }
}
