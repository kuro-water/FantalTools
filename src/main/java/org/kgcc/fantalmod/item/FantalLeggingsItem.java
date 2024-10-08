package org.kgcc.fantalmod.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.kgcc.fantalmod.material.FantalArmorMaterial;

import static org.kgcc.fantalmod.FantalMod.MODID;

public class FantalLeggingsItem extends ArmorItem {
    public FantalLeggingsItem() {
        super(new FantalArmorMaterial(), ArmorItem.Type.LEGGINGS, new Item.Settings().rarity(Rarity.COMMON));
    }

    public static Item register(Item instance, String path) {
        return Registry.register(Registries.ITEM, new Identifier(MODID, path), instance);
    }
}
