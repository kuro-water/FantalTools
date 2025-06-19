package org.kgcc.fantalmod.effect;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LightingStatusEffect extends StatusEffect {
    public LightingStatusEffect() {
        super(
                StatusEffectCategory.BENEFICIAL, // beneficial：良い効果 harmful：悪い効果 neutral：どちらでもない
                0xFF0000); // color in RGB
    }
    
    private BlockState preBlockState;
    private BlockPos preBlockPos;
    
    // This method is called every tick to check whether it should apply the status effect or not
    // このメソッドは、ステータス効果を適用するかどうかをチェックするために毎回呼び出されます
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the status effect every tick.
        // 毎tick適用されてほしい場合には、trueを返すようにします
        return true;
    }
    
    // This method is called when it applies the status effect. We implement custom functionality here.
    // このメソッドは、ステータス効果が適用されたときに呼び出されます。ここでカスタム機能を実装します
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.world.isClient()) {
            return;
        }
        BlockPos pos = entity.getBlockPos().up();
        
        // 高さ制限をチェック
        if (!entity.world.isInBuildLimit(pos)) {
            return; // 高さ制限外の場合は処理を中断
        }
        
        if (!entity.world.getBlockState(pos).isAir()) {
            // 空気でなければ処理を中断
            return;
        }
        if (preBlockPos != null && preBlockPos.equals(pos)) {
            // 前回設置した場所と同じなら処理を中断
            return;
        }
        
        World world = entity.world;
        
        if (preBlockPos != null
                && preBlockState != null
                && world.getBlockState(preBlockPos).equals(Blocks.LIGHT.getDefaultState())) {
            // 一つ前の場所を元に戻す
            // Blocks.LIGHT.getDefaultState()と一致しない場合、ラグやコマンドなどでブロックが設置されているということ。
            // 置き換えてしまうとブロックが消えてしまうので、置き換えない
            world.setBlockState(preBlockPos, preBlockState);
        }
        
        preBlockPos = pos;
        preBlockState = world.getBlockState(pos);
        // 光る空気を設置するっぽい。
        world.setBlockState(pos, Blocks.LIGHT.getDefaultState());
    }
    
    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        // ステータス効果が削除されたときに呼び出されます
        // ここで、前回のブロックを元に戻す処理を行います
        if (entity.world.isClient()) {
            return;
        }
        if (preBlockPos == null
                || preBlockState == null
                || !entity.world.getBlockState(preBlockPos).equals(Blocks.LIGHT.getDefaultState())) {
            return;
        }
        // 一つ前の場所を元に戻す
        // Blocks.LIGHT.getDefaultState()と一致しない場合、ラグやコマンドなどでブロックが設置されているということ。
        // 置き換えてしまうとブロックが消えてしまうので、置き換えない
        entity.world.setBlockState(preBlockPos, preBlockState);
        preBlockPos = null;
        preBlockState = null;
    }
}
