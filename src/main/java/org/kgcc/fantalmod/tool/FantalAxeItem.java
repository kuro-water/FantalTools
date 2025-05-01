package org.kgcc.fantalmod.tool;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.kgcc.fantalmod.test.BaseSkill;
import org.kgcc.fantalmod.test.HealthBoostSkill;
import org.kgcc.fantalmod.util.FantalStateManager;

public class FantalAxeItem extends AxeItem {
    public BaseSkill skill = new HealthBoostSkill();
    
    public FantalAxeItem() {
        super(new FantalToolMaterial(), 5f, -3f, new Settings().rarity(Rarity.COMMON));
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var result = skill.use(world, user, hand);
        if (result.getResult() == ActionResult.PASS) {
            return super.use(world, user, hand);
        }
        return result;
    }
    
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var result = skill.useOnBlock(context);
        if (result == ActionResult.PASS) {
            return super.useOnBlock(context);
        }
        return result;
    }
}
