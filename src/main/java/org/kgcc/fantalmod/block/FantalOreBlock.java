package org.kgcc.fantalmod.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;

public final class FantalOreBlock extends Block {
    public FantalOreBlock(float strength) {
        super(FabricBlockSettings.of(Material.STONE).strength(strength,3.0f).requiresTool());
    }
}
