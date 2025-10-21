package org.kgcc.fantalmod.entity.projectile;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.kgcc.fantalmod.init.ModEntities;
import org.kgcc.fantalmod.registry.FantalModItems;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.DustParticleEffect;
import org.joml.Vector3f;



public class FantalArrowEntity extends PersistentProjectileEntity {

    // このコンストラクタはMODの読み込み時に使われる
    public FantalArrowEntity(EntityType<? extends FantalArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    // これが弓から撃たれた時に呼ばれるコンストラクタ
    public FantalArrowEntity(World world, LivingEntity owner) {
        super(ModEntities.FANTAL_ARROW_ENTITY, owner, world);
    }

    // アイテムのNBTデータ（エンチャントやポーション効果）を矢に引き継ぐ
    public void initFromStack(ItemStack stack) {
        int powerLevel = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
        if (powerLevel > 0) {
            this.setDamage(this.getDamage() + (double) powerLevel * 0.5 + 0.5);
        }
    }

    // 地面に刺さった矢を回収した時に、どのアイテムになるかを定義
    @Override
    protected ItemStack asItemStack() {
        // FantalModItems.FANTAL_ARROW の部分は、あなたの矢のアイテム名に書き換えてください
        return new ItemStack(FantalModItems.FANTAL_ARROW);
    }

    // 敵に当たった時の処理
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        if (entityHitResult.getEntity() instanceof LivingEntity target) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1));
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) {
            // 🎨 黄色のDUSTパーティクル（光るビームっぽい）
            this.getWorld().addParticle(
                    new DustParticleEffect(new Vector3f(1.0f, 1.0f, 0.0f), 1.2f),
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    0.0, 0.0, 0.0
            );


            // 🔥 ランダムで炎を追加（スピード感）
            if (this.getWorld().random.nextFloat() < 0.5f) {
                this.getWorld().addParticle(
                        ParticleTypes.FLAME,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        0.0, 0.0, 0.0
                );
            }

            // ⚡ ランダムでエネルギーのような粒子
            if (this.getWorld().random.nextFloat() < 0.3f) {
                this.getWorld().addParticle(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        0.0, 0.0, 0.0
                );
            }

            // 💥 たまに爆発のようなキラキラ
            if (this.getWorld().random.nextFloat() < 0.2f) {
                this.getWorld().addParticle(
                        ParticleTypes.CRIT,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        0.0, 0.0, 0.0
                );
            }
        }
    }



}