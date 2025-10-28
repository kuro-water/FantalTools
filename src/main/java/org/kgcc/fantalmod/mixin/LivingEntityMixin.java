package org.kgcc.fantalmod.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.jetbrains.annotations.Nullable;
import org.kgcc.fantalmod.registry.FantalModStatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract boolean hasStatusEffect(StatusEffect effect);
    
    @Shadow
    @Nullable
    public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);
    
    @Inject(at = @At("HEAD"), method = "jump()V")
    private void jump(CallbackInfo info) {
        if (this.hasStatusEffect(FantalModStatusEffects.SHACKLES_CURSE_STATUS_EFFECT)) {
            LivingEntity entity = (LivingEntity) (Object) this;
            (entity).damage(
                    entity.getDamageSources().magic(),
                    this.getStatusEffect(FantalModStatusEffects.SHACKLES_CURSE_STATUS_EFFECT).getAmplifier() + 1);
        }
    }
}