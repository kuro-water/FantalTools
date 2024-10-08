package org.kgcc.modid;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static org.kgcc.modid.FantalMod.MODID;

public class FantalShovelItem extends Item {
    public FantalShovelItem() {
        super(new Settings().rarity(Rarity.COMMON));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos().offset(context.getSide());
        ItemStack stack = context.getStack();
        Hand hand = context.getHand();
        PlayerEntity player = context.getPlayer();

        // 松明を設置
        if (!world.isClient() && hand == Hand.MAIN_HAND && world.isAir(pos)) {
            world.setBlockState(pos, Blocks.TORCH.getDefaultState());
            FantalStateManager.incrementFantalPollution(world, player);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static Item register(Item instance, String path) {
        return Registry.register(Registries.ITEM, new Identifier(MODID, path), instance);
    }
}
