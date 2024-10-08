package org.kgcc.modid.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static org.kgcc.modid.FantalMod.MODID;
import static org.kgcc.modid.test.TestItem.TUTORIAL_ARMOR_MATERIAL;

public class FantalHelmetItem extends ArmorItem {
    public FantalHelmetItem() {

        super(TUTORIAL_ARMOR_MATERIAL, Type.HELMET, new Item.Settings());
    }

    public static Item register(Item instance, String path) {
        return Registry.register(Registries.ITEM, new Identifier(MODID, path), instance);
    }
}
