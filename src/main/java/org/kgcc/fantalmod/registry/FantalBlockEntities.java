package org.kgcc.fantalmod.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.fantalgui.SiroanBlockEntity;
import org.kgcc.fantalmod.fantalgui.SiroanBlockScreen;
import org.kgcc.fantalmod.gui.GemInfusingBlockEntity;

public class FantalBlockEntities {
    public static BlockEntityType<GemInfusingBlockEntity> GEM_INFUSING_STATION;
    public static BlockEntityType<SiroanBlockEntity> SIROAN_BLOCK;

    public static void registerBlockEntities() {
        GEM_INFUSING_STATION = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(FantalMod.MODID, "gem_infusing_station"),
                FabricBlockEntityTypeBuilder.create(GemInfusingBlockEntity::new,
                        FantalModItems.GEM_INFUSING_STATION).build(null));


        SIROAN_BLOCK = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(FantalMod.MODID, "siroan_block"),
                FabricBlockEntityTypeBuilder.create(SiroanBlockEntity::new,
                        FantalModItems.SIROAN_BLOCK).build(null));
    }
}
