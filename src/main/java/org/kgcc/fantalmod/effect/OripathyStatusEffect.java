package org.kgcc.fantalmod.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class OripathyStatusEffect extends StatusEffect {
    public OripathyStatusEffect() {
        super(
                StatusEffectCategory.HARMFUL, // beneficial：良い効果 harmful：悪い効果 neutral：どちらでもない
                0xFF0000); // color in RGB
    }
    
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
        // InGameHudMixinで処理するので、ここでは何もしません
    }
}
