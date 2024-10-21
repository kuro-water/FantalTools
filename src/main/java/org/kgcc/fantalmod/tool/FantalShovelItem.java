package org.kgcc.fantalmod.tool;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.kgcc.fantalmod.util.FantalStateManager;

public class FantalShovelItem extends ShovelItem {
    public FantalShovelItem() {
        super(new FantalToolMaterial(), 1.5f, -3f, new Settings().rarity(Rarity.COMMON));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos().offset(context.getSide());
        Hand hand = context.getHand();
        PlayerEntity player = context.getPlayer();
        if (player == null) {
            return ActionResult.FAIL;
        }

        // 松明を設置
        if (!world.isClient() && hand == Hand.MAIN_HAND && world.isAir(pos)) {
            ItemStack torchStack = new ItemStack(Blocks.TORCH);
            BlockItem blockItem = (BlockItem) torchStack.getItem();
            BlockHitResult hitResult = new BlockHitResult(
                    player.getPos(), // Player's position
                    context.getSide(), // Side of the block that was hit
                    pos, // Position of the block
                    false // Whether the hit is inside the block
            );

            // 設置処理
            ActionResult result = blockItem.place(new ItemPlacementContext(player, hand, torchStack, hitResult));
            if (result.isAccepted()) {
                FantalStateManager.addFantalPollution(world, player, 1);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.FAIL;
    }
}
