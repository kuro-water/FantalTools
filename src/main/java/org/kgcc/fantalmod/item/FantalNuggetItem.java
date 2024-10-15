package org.kgcc.fantalmod.item;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static org.kgcc.fantalmod.FantalMod.MODID;

public class FantalNuggetItem extends Item {
    public FantalNuggetItem() {
        super(new Settings());
    }

    public static Item register(Item instance, String path) {
        return Registry.register(Registries.ITEM, new Identifier(MODID, path), instance);
    }
}
