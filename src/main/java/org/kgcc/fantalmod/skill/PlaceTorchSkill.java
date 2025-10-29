package org.kgcc.fantalmod.skill;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.kgcc.fantalmod.util.FantalStateManager;

import java.util.List;

public class PlaceTorchSkill implements BaseSkill {
    private final List<Tool> TOOLS = List.of(Tool.PICKAXE, Tool.AXE, Tool.SHOVEL, Tool.HOE, Tool.SWORD);
    
    @Override
    public List<Tool> getTools() {
        return TOOLS;
    }
    
    @Override
    public String getTranslationKey() {
        return "place_torch";
    }
    
    @Override
    public MutableText getName() {
        return Text.translatable("skill.fantalmod.place_torch");
    }
    
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos().offset(context.getSide());
        Hand hand = context.getHand();
        PlayerEntity player = context.getPlayer();
        if (player == null) {
            return ActionResult.FAIL;
        }
        
        // 松明を設置
        if (!world.isClient() && world.isAir(pos)) {
            var server = world.getServer();
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
                FantalStateManager.addFantalPollution(server, player, 1);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.FAIL;
    }
}
