package org.kgcc.modid;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.RenderLayer;

public final class TestBlock extends Block {
    public TestBlock(Settings settings) {
        super(settings);
    }

    private static final String MODID = System.getProperty("modid");

    public static final Block EXAMPLE_BLOCK = register(new Block(FabricBlockSettings.of(Material.METAL)
                                                                                    .strength(4.0f)), "test_block") ;

    public static final Block ANIME_BLOCK = register(new Block(FabricBlockSettings.of(Material.METAL)
            .strength(4.0f)), "anime_block");

    public static final Block CRYSTAL_BLOCK = register(new AmethystClusterBlock(7,3,FabricBlockSettings.of(Material.AMETHYST)
            .nonOpaque().ticksRandomly().sounds(BlockSoundGroup.AMETHYST_CLUSTER).strength(1.5F)), "crystal_block");

    public static final Block CLEAR_BLOCK = register(new GlassBlock(Settings.of(Material.GLASS).strength(0.3F).sounds(BlockSoundGroup.GLASS)
            .nonOpaque().allowsSpawning(Blocks::never).solidBlock(Blocks::never).suffocates(Blocks::never).blockVision(Blocks::never)),"clear_block");

    private static Block register(Block block, String path) {
        Registry.register(Registries.ITEM, new Identifier(MODID, path), new BlockItem(block, new Item.Settings()));
        return Registry.register(Registries.BLOCK, new Identifier(MODID, path), block);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // 当たり判定
        return VoxelShapes.cuboid(0.0, 0.0, 0.25, 1.0, 1.0, 0.75);
    }

    public static void initialize() {
    }
}
