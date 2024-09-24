package org.kgcc.modid;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class FantalIngotItem extends Item {
    public FantalIngotItem() {
        super(new Settings());
    }

    private static final String MODID = System.getProperty("modid");

    public static Item register(Item instance, String path) {
        return Registry.register(Registries.ITEM, new Identifier(MODID, path), instance);
    }
}
