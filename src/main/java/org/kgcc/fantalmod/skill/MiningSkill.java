package org.kgcc.fantalmod.skill;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OperatorBlock;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.util.FantalStateManager;

import java.util.List;

public class MiningSkill implements BaseSkill {
    private final List<Tool> TOOLS = List.of(Tool.PICKAXE, Tool.AXE, Tool.SHOVEL, Tool.HOE, Tool.SWORD);
    
    @Override
    public List<Tool> getTools() {
        return TOOLS;
    }
    
    @Override
    public String getTranslationKey() {
        return "mining";
    }
    
    @Override
    public MutableText getName() {
        return Text.translatable("skill.fantalmod.mining");
    }
    
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
            // 岩盤やポータルはHardnessが-1
            FantalMod.LOGGER.info("getHardness");
            return ActionResult.PASS;
        }
        
        var result = world.breakBlock(context.getBlockPos(), true, player);
        if (!result) {
            return ActionResult.FAIL;
        }
        
        // 汚染度増加
        var server = world.getServer();
        FantalStateManager.addFantalPollution(server, player, 2);
        
        // 耐久値を減らす
//        var hand = context.getHand();
//        ItemStack stack = player.getStackInHand(hand);
//        stack.damage(1, player, (e) -> e.sendToolBreakStatus(hand));
//
        // CT1秒
//        player.getItemCooldownManager().set(player.getMainHandStack().getItem(), 20);
        
        return ActionResult.SUCCESS;
    }
}
