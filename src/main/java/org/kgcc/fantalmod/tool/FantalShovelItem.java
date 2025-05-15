package org.kgcc.fantalmod.tool;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.kgcc.fantalmod.skill.BaseSkill;
import org.kgcc.fantalmod.skill.PlaceTorch;

public class FantalShovelItem extends ShovelItem {
    public BaseSkill skill = new PlaceTorch();
    
    public FantalShovelItem() {
        super(new FantalToolMaterial(), 1.5f, -3f, new Settings().rarity(Rarity.COMMON));
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // まず基底クラスのuseを呼ぶ
        // SUCCESSが返ってきたなら、アニメーションとかあるっぽいのでそのまま返す
        // それ以外はスキルのuseを呼ぶ
        var result = super.use(world, user, hand);
        if (result.getResult() == ActionResult.SUCCESS) {
            return result;
        }
        return skill.use(world, user, hand);
    }
    
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        // まず基底クラスのuseを呼ぶ
        // SUCCESSが返ってきたなら、アニメーションとかあるっぽいのでそのまま返す
        // それ以外はスキルのuseを呼ぶ
        var result = super.useOnBlock(context);
        if (result == ActionResult.SUCCESS) {
            return result;
        }
        return skill.useOnBlock(context);
    }
}
