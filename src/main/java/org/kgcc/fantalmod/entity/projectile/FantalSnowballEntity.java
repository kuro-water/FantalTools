package org.kgcc.fantalmod.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.kgcc.fantalmod.init.ModEntities;

public class FantalSnowballEntity extends SnowballEntity {

    public FantalSnowballEntity(EntityType<? extends SnowballEntity> entityType, World world) {
        super(entityType, world);
    }

    public FantalSnowballEntity(World world, LivingEntity owner) {
        super(ModEntities.FANTAL_SNOWBALL_ENTITY, world);
        this.setOwner(owner);
        this.updatePosition(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        if (!this.getWorld().isClient) {
            if (entityHitResult.getEntity() instanceof LivingEntity target) {
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1));
            }
        }
    }
}
