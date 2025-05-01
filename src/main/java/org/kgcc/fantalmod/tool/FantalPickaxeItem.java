package org.kgcc.fantalmod.tool;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.kgcc.fantalmod.test.BaseSkill;
import org.kgcc.fantalmod.test.HasteSkill;
import org.kgcc.fantalmod.util.FantalStateManager;

public class FantalPickaxeItem extends PickaxeItem {
    public BaseSkill skill = new HasteSkill();
    
    public FantalPickaxeItem() {
        super(new FantalToolMaterial(), 1, -2.8f, new Settings().rarity(Rarity.COMMON));
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return skill.use(world, user, hand);
    }
    
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return skill.useOnBlock(context);
    }
}
