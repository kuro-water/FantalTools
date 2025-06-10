package org.kgcc.fantalmod.tool;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.kgcc.fantalmod.registry.FantalModSkills;
import org.kgcc.fantalmod.skill.BaseSkill;
import org.kgcc.fantalmod.skill.HealthBoostSkill;

public class FantalAxeItem extends AxeItem implements FantalTool {
    private BaseSkill skill = FantalModSkills.HEALTH_BOOST;
    
    @Override
    public void setSkill(BaseSkill skill) {
        this.skill = skill;
    }
    
    @Override
    public BaseSkill getSkill() {
        return skill;
    }
    
    public FantalAxeItem() {
        super(new FantalToolMaterial(), 5f, -3f, new Settings().rarity(Rarity.COMMON));
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
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        skill.inventoryTick(stack, world, entity, slot, selected);
    }
}
