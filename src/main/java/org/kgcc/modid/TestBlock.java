package org.kgcc.modid;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class TestBlock extends Block {
    public TestBlock(Settings settings) {
        super(settings);
    }

    private static final String MODID = System.getProperty("modid");

    public static final Block EXAMPLE_BLOCK = register(new Block(FabricBlockSettings.of(Material.METAL)
                                                                                    .strength(4.0f)), "test_block");

    public static final BlockEntityType<BoxBlockEntity> BOX_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("tutorial", "box_block"),
            BlockEntityType.Builder.create(BoxBlockEntity::new, EXAMPLE_BLOCK).build(null));

    private static Block register(Block block, String path) {
        Registry.register(Registries.ITEM, new Identifier(MODID, path), new BlockItem(block, new Item.Settings()));
        return Registry.register(Registries.BLOCK, new Identifier(MODID, path), block);
    }

    public static void initialize() {
    }
}
