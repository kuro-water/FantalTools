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
     */
    public ActionResult useOnBlock(ItemUsageContext context) {
        var world = context.getWorld();
        var player = context.getPlayer();
        if (world.isClient() || player == null) {
            return ActionResult.PASS;
        }
        BlockPos blockPos = context.getBlockPos();

//        FantalMod.LOGGER.info(String.valueOf(((ServerPlayerEntity) player).interactionManager.tryBreakBlock(blockPos)));
        
        BlockState blockState = world.getBlockState(blockPos);
        if (!player.getMainHandStack().getItem().canMine(blockState, world, blockPos, player)) {
            FantalMod.LOGGER.info("canMineがfalse");
            return ActionResult.PASS;
        }
        Block block = blockState.getBlock();
        if (block instanceof OperatorBlock && !player.isCreativeLevelTwoOp()) {
            world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
            FantalMod.LOGGER.info("OperatorBlock");
            return ActionResult.PASS;
        }
        GameMode gameMode = ((ServerPlayerEntity) player).interactionManager.getGameMode();
        if (player.isBlockBreakingRestricted(world, blockPos, gameMode)) {
            FantalMod.LOGGER.info("isBlockBreakingRestricted");
            return ActionResult.PASS;
        }
        if (blockState.getHardness(world, blockPos) < 0) {// !blockState.isAir()
            // 岩盤や黒曜石はHardnessが-1
            FantalMod.LOGGER.info("getHardness");
            return ActionResult.PASS;
        }
        
        // getDroppedStacksだとチェストの中身などは考慮されない
        // シルクタッチも考慮されない
//        var items = Block.getDroppedStacks(blockState, (ServerWorld) world, blockPos, null);
//        items.forEach(stack -> FantalMod.LOGGER.info(stack.toString()));
        
        var result = world.breakBlock(context.getBlockPos(), true, player);
        if (!result) {
            return ActionResult.FAIL;
        }
        // Block.onBrokenとafterBreakは本来は自動で（バニラ側で）呼び出されるぽい
        // でもworld.breakBlockでは呼ばれないので呼んどいた方がよさそう？
        // onBrokenの説明には「可能な限りAbstractBlock.onStateReplacedまたはAbstractBlock.onStacksDroppedを使うべき」と書いてあるが、それらは非推奨となっている。説明によると「非推奨メソッドは、AbstractBlock.AbstractBlockState の対応するメソッドまたはこのクラスのサブクラスからのみ呼び出されるべきであることを意味します。」らしい。
//        block.onBroken(world, blockPos, blockState);
        // afterBreakはonBrokenの後に呼ばれるぽい
        // 蜂が怒ったりするはずなんだけど怒らないなぁ
        // ドロップにも関係しているらしい。World.breakBlockがdrop=falseでもドロップする
//        block.afterBreak(world, player, blockPos, blockState, null, player.getMainHandStack());

//        var res1 = PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(
//                world,
//                player,
//                blockPos,
//                world.getBlockState(blockPos),
//                world.getBlockEntity(blockPos));
        // tryBreakBlockだと適正ツールでないと壊せない
//        var res2 = ((ServerPlayerEntity) player).interactionManager.tryBreakBlock(blockPos);
//
//        if (!res1 || !res2) {
//            return ActionResult.FAIL;
//        }
        
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
//    public ActionResult useOnBlock(ItemUsageContext context) {
//        var world = context.getWorld();
//        var player = context.getPlayer();
//        var hand = context.getHand();
//        if (world.isClient() || player == null) {
//            return ActionResult.PASS;
//        }
//
//        var blockPos = context.getBlockPos();
//        var blockState = world.getBlockState(blockPos);
//
//        if (blockState.isAir()) {
//            return ActionResult.PASS;
//        }
//
//        // ブロックが破壊可能かどうかを確認
//        if (!blockState.isAir() && blockState.getHardness(world, blockPos) >= 0) {
//            var block = blockState.getBlock();
//            var toolStack = player.getStackInHand(hand);
//
//            // ブロックを破壊し、適正ツールで破壊したときの挙動を再現
//            block.afterBreak(world, player, blockPos, blockState, null, toolStack);
//            world.breakBlock(blockPos, false, player);
////            var drop = Block.getDroppedStacks(blockState, (ServerWorld) world, blockPos, null, player, toolStack);
////
////            drop.forEach(stack -> {
////                ItemScatterer.spawn(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), stack);
////            });
//
////            ItemScatterer.spawn(world, blockPos, (Inventory) drop);
//        }
//
//
//        return ActionResult.SUCCESS;
//    }
}
