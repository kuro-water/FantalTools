package org.kgcc.fantalmod.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;

public final class FantalBlock extends Block {
    public FantalBlock() {
        super(FabricBlockSettings.of(Material.STONE).strength(1f).requiresTool());
    }
}
