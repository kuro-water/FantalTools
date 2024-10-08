package org.kgcc.fantal_mod.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.kgcc.fantal_mod.material.FantalArmorMaterial;

import static org.kgcc.fantal_mod.FantalMod.MODID;

public class FantalBootsItem extends ArmorItem {
    public FantalBootsItem() {
        super(new FantalArmorMaterial(), Type.BOOTS, new Item.Settings());
    }

    public static Item register(Item instance, String path) {
        return Registry.register(Registries.ITEM, new Identifier(MODID, path), instance);
    }
}
