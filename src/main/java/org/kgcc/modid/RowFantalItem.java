package org.kgcc.modid;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static org.kgcc.modid.FantalMod.MODID;

public class RowFantalItem extends Item {
    public RowFantalItem() {
        super(new Settings());
    }

    public static Item register(Item instance, String path) {
        return Registry.register(Registries.ITEM, new Identifier(MODID, path), instance);
    }
}
