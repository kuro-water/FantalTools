package org.kgcc.fantalmod.skill;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public interface BaseSkill {
    // todo: コマンドでスキル付与
    // todo: コンフィグかなんかで、Shiftで無効にする設定
    // todo: UNBREAKING（耐久18削れるごとに侵食1,ツルハシ折れるくらいで侵食100）とかどうかな
    
    /**
     * <p>このスキルが対応しているツールの種類であるenumのリストを返す。</p>
     * <p>interfaceでメンバ定義が出来ないため、getTools()メソッドを定義する。</p>
     */
    List<Tool> getTools();
    
    /**
     * {@link #isToolSupported(Tool)}のラッパー関数。
     * @see #isToolSupported(Tool)
     */
    default boolean isToolSupported(Item item) {
        if (item instanceof PickaxeItem) {
            return isToolSupported(Tool.PICKAXE);
        } else if (item instanceof AxeItem) {
            return isToolSupported(Tool.AXE);
        } else if (item instanceof ShovelItem) {
            return isToolSupported(Tool.SHOVEL);
        } else if (item instanceof HoeItem) {
            return isToolSupported(Tool.HOE);
        } else if (item instanceof SwordItem) {
            return isToolSupported(Tool.SWORD);
        }
        return false;
    }
    
    /**
     * <p>指定されたツールがこのスキルに対応しているかどうかを返す。
     * ツールの種類は、{@link Tool} enumで定義されている。</p>
     * <p>true: 対応している、false: 対応していない</p>
     */
    default boolean isToolSupported(Tool tool) {
        return getTools().stream().anyMatch(_tool -> _tool == tool);
    }
    
    /**
     * <p>スキルの翻訳キーを返す。
     * 大体は{@link #getName()}ないし{@link #getTooltip()}で解決すると思う。</p>
     * <p>interfaceでメンバ定義が出来ないため、getTools()メソッドを定義する。</p>
     */
    String getTranslationKey();
    
    /**
     * <p>ゲーム中に表示されるべきスキルの名前を返す。
     * {@link #getTranslationKey()}を元に適切な言語で取得する。</p>
     */
    default MutableText getName() {
        return Text.translatable("skill.fantalmod." + getTranslationKey());
    }
    
    /**
     * <p>ゲーム中に表示されるべきスキルの説明を返す。
     * {@link #getTranslationKey()}を元に適切な言語で取得する。</p>
     * <p>{@link org.kgcc.fantalmod.block.FantalBench}のGUI上でのツールチップとして表示される想定。</p>
     */
    default MutableText getTooltip() {
        return Text.translatable("skill.fantalmod." + getTranslationKey() + ".description");
    }
    
    /**
     * @see Item#use(World, PlayerEntity, Hand)
     */
    default TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
    
    /**
     * @see Item#useOnBlock(ItemUsageContext)
     */
    default ActionResult useOnBlock(ItemUsageContext context) {
        return ActionResult.PASS;
    }
    
    /**
     * @see Item#inventoryTick(ItemStack, World, Entity, int, boolean)
     * */
    default void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
    }
    
    /**
     * @see Item#appendTooltip(ItemStack, World, List, TooltipContext)
     */
    default void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(getName().formatted(Formatting.AQUA));
    }
}
