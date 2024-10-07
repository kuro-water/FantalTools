package org.kgcc.modid;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static org.kgcc.modid.ExampleMod.MODID;
import static org.kgcc.modid.TestItem.TUTORIAL_ARMOR_MATERIAL;

public class TutorialBootsItem extends ArmorItem {
    public TutorialBootsItem() {

        super(TUTORIAL_ARMOR_MATERIAL, Type.BOOTS, new Item.Settings());
    }

    public static Item register(Item instance, String path) {
        return Registry.register(Registries.ITEM, new Identifier(MODID, path), instance);
    }
}
