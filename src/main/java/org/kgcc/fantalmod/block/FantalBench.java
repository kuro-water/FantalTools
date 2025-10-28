package org.kgcc.fantalmod.block;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.kgcc.fantalmod.entity.FantalBenchEntity;
import org.kgcc.fantalmod.screen.FantalBenchScreenHandler;

public class FantalBench extends BlockWithEntity implements BlockEntityProvider {
    
    public static final IntProperty APPEARANCE = IntProperty.of("appearance", 0, 5);
    
    public FantalBench(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(APPEARANCE, 0));
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<net.minecraft.block.Block, BlockState> builder) {
        builder.add(APPEARANCE);
    }
    
    /* BLOCK ENTITY */
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FantalBenchEntity) {
                ItemScatterer.spawn(world, pos, (FantalBenchEntity) blockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FantalBenchEntity) {
                player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                    @Override
                    public void writeScreenOpeningData(net.minecraft.server.network.ServerPlayerEntity player, PacketByteBuf buf) {
                        buf.writeBlockPos(pos);
                    }
                    
                    @Override
                    public Text getDisplayName() {
                        return Text.translatable("block.fantalmod.fantal_bench");
                    }
                    
                    @Nullable
                    @Override
                    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                        return new FantalBenchScreenHandler(syncId, inv, (FantalBenchEntity) blockEntity);
                    }
                });
            }
        }
        
        return ActionResult.SUCCESS;
    }
    
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FantalBenchEntity(pos, state);
    }
    
    // ...existing code...
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        int appearance = state.get(APPEARANCE); // BlockStateのプロパティから取得
        switch (appearance) {
            case 0: {
                VoxelShape shape = VoxelShapes.empty();
                VoxelShape base = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
                shape = VoxelShapes.union(shape, base);
                
                VoxelShape leg1 = VoxelShapes.cuboid(0.2, 0.5, 0.2, 0.8, 0.67, 0.8);
                shape = VoxelShapes.union(shape, leg1);
                
                
                return shape;// Normal
            }
            case 1: {
                
                VoxelShape shape = VoxelShapes.empty();
                VoxelShape base = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
                shape = VoxelShapes.union(shape, base);
                
                VoxelShape leg1 = VoxelShapes.cuboid(0.2, 0.5, 0.2, 0.8, 0.67, 0.8);
                shape = VoxelShapes.union(shape, leg1);
                
                VoxelShape leg2 = VoxelShapes.cuboid(0.45, 0.45, 0.45, 0.55, 1.25, 0.55);
                shape = VoxelShapes.union(shape, leg2);
                return shape;// Normal
            }// Pickaxe
            
            case 2: {// Axe
                VoxelShape shape = VoxelShapes.empty();
                VoxelShape base = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
                shape = VoxelShapes.union(shape, base);
                
                VoxelShape leg1 = VoxelShapes.cuboid(0.2, 0.5, 0.2, 0.8, 0.67, 0.8);
                shape = VoxelShapes.union(shape, leg1);
                
                VoxelShape leg2 = VoxelShapes.cuboid(0.45, 0.45, 0.45, 0.55, 1.25, 0.55);
                shape = VoxelShapes.union(shape, leg2);
                return shape;// Normal
            }
            
            case 3: {
                VoxelShape shape = VoxelShapes.empty();
                VoxelShape base = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
                shape = VoxelShapes.union(shape, base);
                
                VoxelShape leg1 = VoxelShapes.cuboid(0.2, 0.5, 0.2, 0.8, 0.67, 0.8);
                shape = VoxelShapes.union(shape, leg1);
                
                VoxelShape leg2 = VoxelShapes.cuboid(0.45, 0.45, 0.45, 0.55, 1.25, 0.55);
                shape = VoxelShapes.union(shape, leg2);
                return shape;// Normal
            }
            case 4: {
                VoxelShape shape = VoxelShapes.empty();
                VoxelShape base = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
                shape = VoxelShapes.union(shape, base);
                
                VoxelShape leg1 = VoxelShapes.cuboid(0.2, 0.5, 0.2, 0.8, 0.67, 0.8);
                shape = VoxelShapes.union(shape, leg1);
                
                VoxelShape leg2 = VoxelShapes.cuboid(0.45, 0.45, 0.45, 0.55, 1.25, 0.55);
                shape = VoxelShapes.union(shape, leg2);
                return shape;// Normal
            }
            case 5: {
                VoxelShape shape = VoxelShapes.empty();
                VoxelShape base = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
                shape = VoxelShapes.union(shape, base);
                
                VoxelShape leg1 = VoxelShapes.cuboid(0.2, 0.5, 0.2, 0.8, 0.67, 0.8);
                shape = VoxelShapes.union(shape, leg1);
                
                VoxelShape leg2 = VoxelShapes.cuboid(0.45, 0.45, 0.45, 0.55, 1.25, 0.55);
                shape = VoxelShapes.union(shape, leg2);
                return shape;// Normal
            }
            default:
                return VoxelShapes.fullCube();
        }
    }
    
    //ブロックの当たり判定（どこがどう対応しているかは不明
//    @Override
//    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
//        VoxelShape shape = VoxelShapes.empty();
//        // 例: 必要な形状を追加
//        VoxelShape base = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
//        shape = VoxelShapes.union(shape, base);
//
//        // 各パーツの当たり判定
//        VoxelShape leg1 = VoxelShapes.cuboid(4.0/16, 9.0/16, 4.0/16, 5.0/16, 10.0/16, 5.0/16);
//        shape = VoxelShapes.union(shape, leg1);
//
//        VoxelShape leg2 = VoxelShapes.cuboid(10.0/16, 9.0/16, 5.0/16, 11.0/16, 10.0/16, 6.0/16);
//        shape = VoxelShapes.union(shape, leg2);
//
//        VoxelShape leg3 = VoxelShapes.cuboid(10.0/16, 10.0/16, 6.0/16, 11.0/16, 11.0/16, 10.0/16);
//        shape = VoxelShapes.union(shape, leg3);
//
//        VoxelShape leg4 = VoxelShapes.cuboid(10.0/16, 9.0/16, 10.0/16, 11.0/16, 10.0/16, 11.0/16);
//        shape = VoxelShapes.union(shape, leg4);
//
//        VoxelShape leg5 = VoxelShapes.cuboid(6.0/16, 10.0/16, 10.0/16, 10.0/16, 11.0/16, 11.0/16);
//        shape = VoxelShapes.union(shape, leg5);
//
//        VoxelShape leg6 = VoxelShapes.cuboid(5.0/16, 9.0/16, 5.0/16, 6.0/16, 10.0/16, 6.0/16);
//        shape = VoxelShapes.union(shape, leg6);
//
//        VoxelShape leg7 = VoxelShapes.cuboid(5.0/16, 10.0/16, 6.0/16, 6.0/16, 11.0/16, 10.0/16);
//        shape = VoxelShapes.union(shape, leg7);
//
//        VoxelShape leg8 = VoxelShapes.cuboid(5.0/16, 9.0/16, 10.0/16, 6.0/16, 10.0/16, 11.0/16);
//        shape = VoxelShapes.union(shape, leg8);
//
//        VoxelShape leg9 = VoxelShapes.cuboid(6.0/16, 10.0/16, 5.0/16, 10.0/16, 11.0/16, 6.0/16);
//        shape = VoxelShapes.union(shape, leg9);
//
//        VoxelShape leg10 = VoxelShapes.cuboid(6.0/16, 9.0/16, 4.0/16, 10.0/16, 10.0/16, 5.0/16);
//        shape = VoxelShapes.union(shape, leg10);
//
//        VoxelShape leg11 = VoxelShapes.cuboid(6.0/16, 8.0/16, 3.0/16, 10.0/16, 9.0/16, 4.0/16);
//        shape = VoxelShapes.union(shape, leg11);
//
//        VoxelShape leg12 = VoxelShapes.cuboid(6.0/16, 9.0/16, 11.0/16, 10.0/16, 10.0/16, 12.0/16);
//        shape = VoxelShapes.union(shape, leg12);
//
//        VoxelShape leg13 = VoxelShapes.cuboid(6.0/16, 8.0/16, 12.0/16, 10.0/16, 9.0/16, 13.0/16);
//        shape = VoxelShapes.union(shape, leg13);
//
//        VoxelShape leg14 = VoxelShapes.cuboid(4.0/16, 9.0/16, 6.0/16, 5.0/16, 10.0/16, 10.0/16);
//        shape = VoxelShapes.union(shape, leg14);
//
//        VoxelShape leg15 = VoxelShapes.cuboid(3.0/16, 8.0/16, 6.0/16, 4.0/16, 9.0/16, 10.0/16);
//        shape = VoxelShapes.union(shape, leg15);
//
//        VoxelShape leg16 = VoxelShapes.cuboid(11.0/16, 9.0/16, 6.0/16, 12.0/16, 10.0/16, 10.0/16);
//        shape = VoxelShapes.union(shape, leg16);
//
//        VoxelShape leg17 = VoxelShapes.cuboid(12.0/16, 8.0/16, 6.0/16, 13.0/16, 9.0/16, 10.0/16);
//        shape = VoxelShapes.union(shape, leg17);
//
//        VoxelShape leg18 = VoxelShapes.cuboid(9.0/16, 8.0/16, 5.0/16, 10.0/16, 9.0/16, 9.0/16);
//        shape = VoxelShapes.union(shape, leg18);
//
//        VoxelShape leg19 = VoxelShapes.cuboid(9.0/16, 9.0/16, 6.0/16, 10.0/16, 10.0/16, 10.0/16);
//        shape = VoxelShapes.union(shape, leg19);
//
//        VoxelShape leg20 = VoxelShapes.cuboid(7.0/16, 8.0/16, 9.0/16, 10.0/16, 9.0/16, 10.0/16);
//        shape = VoxelShapes.union(shape, leg20);
//
//        VoxelShape leg21 = VoxelShapes.cuboid(7.0/16, 9.0/16, 9.0/16, 9.0/16, 10.0/16, 10.0/16);
//        shape = VoxelShapes.union(shape, leg21);
//
//        VoxelShape leg22 = VoxelShapes.cuboid(6.0/16, 8.0/16, 6.0/16, 9.0/16, 9.0/16, 7.0/16);
//        shape = VoxelShapes.union(shape, leg22);
//
//        VoxelShape leg23 = VoxelShapes.cuboid(6.0/16, 9.0/16, 6.0/16, 7.0/16, 10.0/16, 10.0/16);
//        shape = VoxelShapes.union(shape, leg23);
//
//        VoxelShape leg24 = VoxelShapes.cuboid(6.0/16, 8.0/16, 7.0/16, 7.0/16, 9.0/16, 10.0/16);
//        shape = VoxelShapes.union(shape, leg24);
//
//        VoxelShape leg25 = VoxelShapes.cuboid(7.0/16, 9.0/16, 6.0/16, 9.0/16, 10.0/16, 7.0/16);
//        shape = VoxelShapes.union(shape, leg25);
//
//        VoxelShape leg26 = VoxelShapes.cuboid(4.0/16, 8.0/16, 4.0/16, 12.0/16, 9.0/16, 5.0/16);
//        shape = VoxelShapes.union(shape, leg26);
//
//        VoxelShape leg27 = VoxelShapes.cuboid(4.0/16, 8.0/16, 5.0/16, 5.0/16, 9.0/16, 12.0/16);
//        shape = VoxelShapes.union(shape, leg27);
//
//        VoxelShape leg28 = VoxelShapes.cuboid(11.0/16, 8.0/16, 5.0/16, 12.0/16, 9.0/16, 11.0/16);
//        shape = VoxelShapes.union(shape, leg28);
//
//        VoxelShape leg29 = VoxelShapes.cuboid(5.0/16, 8.0/16, 11.0/16, 12.0/16, 9.0/16, 12.0/16);
//        shape = VoxelShapes.union(shape, leg29);
//
//        VoxelShape leg30 = VoxelShapes.cuboid(4.0/16, 9.0/16, 11.0/16, 5.0/16, 10.0/16, 12.0/16);
//        shape = VoxelShapes.union(shape, leg30);
//
//        VoxelShape leg31 = VoxelShapes.cuboid(11.0/16, 9.0/16, 11.0/16, 12.0/16, 10.0/16, 12.0/16);
//        shape = VoxelShapes.union(shape, leg31);
//
//        VoxelShape leg32 = VoxelShapes.cuboid(11.0/16, 9.0/16, 4.0/16, 12.0/16, 10.0/16, 5.0/16);
//        shape = VoxelShapes.union(shape, leg32);
//
//        return shape;
//    }

}