package org.kgcc.fantalmod.tool;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.kgcc.fantalmod.registry.FantalModSkills;
import org.kgcc.fantalmod.skill.BaseSkill;

public class FantalAxeItem extends AxeItem implements FantalToolItem {
    private BaseSkill skill = FantalModSkills.HEALTH_BOOST;
    
    @Override
    public void setSkill(ItemStack stack, @NotNull BaseSkill skill) {
        this.skill = skill;
        FantalToolItem.writeNbt(stack, skill.getName().getString());
    }
    
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
        ItemStack itemStack = user.getStackInHand(hand);
        skill = FantalModSkills.SKILLS
                .stream()
                .filter(s -> s.getName().getString().equals(FantalToolItem.readNbt(itemStack)))
                .findFirst()
                .orElse(FantalModSkills.HEALTH_BOOST);
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
        // todo:もっときれいに解決したい
        PlayerEntity player = context.getPlayer();
        if(player == null) {
            return result;
        }
        Hand hand = context.getHand();
        ItemStack itemStack = player.getStackInHand(hand);
        skill = FantalModSkills.SKILLS
                .stream()
                .filter(s -> s.getName().getString().equals(FantalToolItem.readNbt(itemStack)))
                .findFirst()
                .orElse(FantalModSkills.HEALTH_BOOST);
        return skill.useOnBlock(context);
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        skill = FantalModSkills.SKILLS
                .stream()
                .filter(s -> s.getName().getString().equals(FantalToolItem.readNbt(stack)))
                .findFirst()
                .orElse(FantalModSkills.HEALTH_BOOST);
        skill.inventoryTick(stack, world, entity, slot, selected);
    }
    
    @Override
    public void appendTooltip(ItemStack stack, World world, java.util.List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        skill.appendTooltip(stack, world, tooltip, context);
    }
}
