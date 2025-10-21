package org.kgcc.fantalmod.test;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.block.ShapeContext;
public class FantalbenchClider extends Block {
    public FantalbenchClider(Settings settings) {
        super(settings);
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(4.0/16, 9.0/16, 4.0/16, 5.0/16, 10.0/16, 5.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(10.0/16, 9.0/16, 5.0/16, 11.0/16, 10.0/16, 6.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(10.0/16, 10.0/16, 6.0/16, 11.0/16, 11.0/16, 10.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(10.0/16, 9.0/16, 10.0/16, 11.0/16, 10.0/16, 11.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(6.0/16, 10.0/16, 10.0/16, 10.0/16, 11.0/16, 11.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(5.0/16, 9.0/16, 5.0/16, 6.0/16, 10.0/16, 6.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(5.0/16, 10.0/16, 6.0/16, 6.0/16, 11.0/16, 10.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(5.0/16, 9.0/16, 10.0/16, 6.0/16, 10.0/16, 11.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(6.0/16, 10.0/16, 5.0/16, 10.0/16, 11.0/16, 6.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(6.0/16, 9.0/16, 4.0/16, 10.0/16, 10.0/16, 5.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(6.0/16, 8.0/16, 3.0/16, 10.0/16, 9.0/16, 4.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(6.0/16, 9.0/16, 11.0/16, 10.0/16, 10.0/16, 12.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(6.0/16, 8.0/16, 12.0/16, 10.0/16, 9.0/16, 13.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(4.0/16, 9.0/16, 6.0/16, 5.0/16, 10.0/16, 10.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(3.0/16, 8.0/16, 6.0/16, 4.0/16, 9.0/16, 10.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(11.0/16, 9.0/16, 6.0/16, 12.0/16, 10.0/16, 10.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(12.0/16, 8.0/16, 6.0/16, 13.0/16, 9.0/16, 10.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(9.0/16, 8.0/16, 5.0/16, 10.0/16, 9.0/16, 9.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(9.0/16, 9.0/16, 6.0/16, 10.0/16, 10.0/16, 10.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(7.0/16, 8.0/16, 9.0/16, 10.0/16, 9.0/16, 10.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(7.0/16, 9.0/16, 9.0/16, 9.0/16, 10.0/16, 10.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(6.0/16, 8.0/16, 6.0/16, 9.0/16, 9.0/16, 7.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(6.0/16, 9.0/16, 6.0/16, 7.0/16, 10.0/16, 10.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(6.0/16, 8.0/16, 7.0/16, 7.0/16, 9.0/16, 10.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(7.0/16, 9.0/16, 6.0/16, 9.0/16, 10.0/16, 7.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(4.0/16, 8.0/16, 4.0/16, 12.0/16, 9.0/16, 5.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(4.0/16, 8.0/16, 5.0/16, 5.0/16, 9.0/16, 12.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(11.0/16, 8.0/16, 5.0/16, 12.0/16, 9.0/16, 11.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(5.0/16, 8.0/16, 11.0/16, 12.0/16, 9.0/16, 12.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(4.0/16, 9.0/16, 11.0/16, 5.0/16, 10.0/16, 12.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(11.0/16, 9.0/16, 11.0/16, 12.0/16, 10.0/16, 12.0/16));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(11.0/16, 9.0/16, 4.0/16, 12.0/16, 10.0/16, 5.0/16));
        // 必要に応じて残りのelementsも追加
        return shape;
    }
}
