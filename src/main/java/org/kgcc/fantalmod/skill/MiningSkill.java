package org.kgcc.fantalmod.skill;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OperatorBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.util.FantalStateManager;

public class MiningSkill implements BaseSkill {
    /**
     * <p>スキルの使用</p>
     * <p>ブロック破壊時の挙動は{@linkplain ServerPlayerInteractionManager#tryBreakBlock}を参考にしている</p>
     *
     */
    public ActionResult useOnBlock(ItemUsageContext context) {
        // todo: クワで石とか壊すとドロップしない
        var world = context.getWorld();
        var player = context.getPlayer();
        if (world.isClient() || player == null) {
            return ActionResult.PASS;
        }
        BlockPos pos = context.getBlockPos();
        
        FantalMod.LOGGER.info(String.valueOf(((ServerPlayerEntity) player).interactionManager.tryBreakBlock(pos)));
        
        BlockState blockState = world.getBlockState(pos);
        if (!player.getMainHandStack().getItem().canMine(blockState, world, pos, player)) {
            return ActionResult.PASS;
        }
        Block block = blockState.getBlock();
        if (block instanceof OperatorBlock && !player.isCreativeLevelTwoOp()) {
            world.updateListeners(pos, blockState, blockState, Block.NOTIFY_ALL);
            return ActionResult.PASS;
        }
        GameMode gameMode = ((ServerPlayerEntity) player).interactionManager.getGameMode();
        if (player.isBlockBreakingRestricted(world, pos, gameMode)) {
            return ActionResult.PASS;
        }
        
        
        var result = world.breakBlock(context.getBlockPos(), true, player);
        if (!result) {
            return ActionResult.FAIL;
        }
        block.onBroken(world, pos, blockState);
        
        // 汚染度増加
        var server = world.getServer();
        FantalStateManager.addFantalPollution(server, player, 1);
        FantalStateManager.sendFantalPollution(server, player);
        
        // 耐久値を減らす
        var hand = context.getHand();
        ItemStack stack = player.getStackInHand(hand);
        stack.damage(1, player, (e) -> e.sendToolBreakStatus(hand));
        
        return ActionResult.SUCCESS;
    }
}
