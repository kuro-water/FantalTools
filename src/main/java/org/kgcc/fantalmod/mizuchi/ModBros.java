package org.kgcc.fantalmod.mizuchi;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBros{
    public static final Block TOGGLE_BLOCK = new ToggleBlock(FabricBlockSettings.of(Material.STONE).strength(1.5f));

    public static void registerBlocks() {
        Registry.register(Registries.BLOCK, new Identifier("fantalmod", "toggle_block"), TOGGLE_BLOCK);
        Registry.register(Registries.ITEM, new Identifier("fantalmod", "toggle_block"),
                new BlockItem(TOGGLE_BLOCK, new FabricItemSettings()));
    }
}
