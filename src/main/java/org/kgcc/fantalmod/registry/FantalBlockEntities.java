package org.kgcc.fantalmod.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.entity.FantalBenchEntity;

public class FantalBlockEntities {
    public static BlockEntityType<FantalBenchEntity> SIROAN_BLOCK;

    public static void registerBlockEntities() {
        SIROAN_BLOCK = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(FantalMod.MODID, "siroan_block"),
                FabricBlockEntityTypeBuilder.create(FantalBenchEntity::new,
                                                    FantalModItems.SIROAN_BLOCK).build(null));
    }
}
