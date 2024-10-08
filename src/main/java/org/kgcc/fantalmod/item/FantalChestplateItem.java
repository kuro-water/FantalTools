package org.kgcc.fantalmod.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.material.FantalArmorMaterial;

import static org.kgcc.fantalmod.FantalMod.MODID;

public class FantalChestplateItem extends ArmorItem {
    public FantalChestplateItem() {
        super(new FantalArmorMaterial(), ArmorItem.Type.CHESTPLATE, new Item.Settings());
    }

    public static Item register(Item instance, String path) {
        return Registry.register(Registries.ITEM, new Identifier(MODID, path), instance);
    }
}
