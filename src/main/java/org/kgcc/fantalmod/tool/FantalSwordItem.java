package org.kgcc.fantalmod.tool;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.kgcc.fantalmod.skill.BaseSkill;
import org.kgcc.fantalmod.skill.NoneSkill;

public class FantalSwordItem extends SwordItem {
    public BaseSkill skill = new NoneSkill();
    
    public FantalSwordItem() {
        super(new FantalToolMaterial(), 3, -2.4f, new Item.Settings().rarity(Rarity.COMMON));
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
