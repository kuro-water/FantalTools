package org.kgcc.modid.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static org.kgcc.modid.FantalMod.MODID;

public final class FantalBlock extends Block {
    public FantalBlock() {
        super(FabricBlockSettings.of(Material.STONE).strength(1f).requiresTool());
    }

    public static Block register(Block block, String path) {
        Registry.register(Registries.ITEM, new Identifier(MODID, path), new BlockItem(block, new Item.Settings()));
        return Registry.register(Registries.BLOCK, new Identifier(MODID, path), block);
    }
}
