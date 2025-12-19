package org.kgcc.fantalmod.tool;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class FantalWandItem extends Item {
    
    // ===== 調整できる定数たち =====
    private static final boolean DEBUG = false;          // 本数/プル率をアクションバー表示
    private static final int MIN_STRIKES = 3;           // 最小本数
    private static final int MAX_STRIKES = 30;          // 最大本数
    private static final int COOLDOWN_TICKS = 40;       // クールダウン（2秒）
    private static final double KNOCKBACK_STRENGTH = 1.0; // ノックバックの強さ
    private static final double KNOCKBACK_Y = 0.30;     // ノックバックの上方向成分
    private static final double MAX_DISTANCE = 30.0;     // 雷の最長距離
    private static final double HORIZONTAL_SPREAD = 10.0;  // 横ブレ幅（±5）
    
    public FantalWandItem() {
        super(new Settings().maxDamage(384)); // 弓と同程度の耐久
    }
    
    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity playerEntity))
            return;
        
        int usedTicks = this.getMaxUseTime(stack) - remainingUseTicks;
        float pullProgress = getPullProgress(usedTicks);
        
        // 早すぎる放しは無効
        if (pullProgress < 0.05f)
            return;
        
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            Random random = world.getRandom();
            Vec3d look = playerEntity.getRotationVec(1.0F);
            Vec3d origin = playerEntity.getPos().add(0, 1.6, 0); // 視線付近
            
            // 通常の本数計算（0～1.0まで）
            int count = MathHelper.clamp(
                    MIN_STRIKES + Math.round(Math.min(pullProgress, 1.0F) * (MAX_STRIKES - MIN_STRIKES)),
                    MIN_STRIKES, MAX_STRIKES
                                        );
            
            // デバッグ表示
            if (DEBUG) {
                String msg = String.format("⚡ Strikes: %d  |  Pull: %.2f", count, pullProgress);
                playerEntity.sendMessage(Text.literal(msg), true);
            }
            
            // ===== 飛距離の計算 =====
            double distance;
            if (pullProgress <= 1.0F) {
                // 通常チャージ
                distance = MAX_DISTANCE * pullProgress;
            } else {
                // オーバーチャージ
                float over = pullProgress - 1.0F;
                distance = MAX_DISTANCE + over * 1.1; // 追加距離スケール（調整可）
                
                // 特殊エフェクト
                serverWorld.spawnParticles(
                        ParticleTypes.END_ROD,
                        origin.x, origin.y, origin.z,
                        10, 0.4, 0.3, 0.4, 0.01
                                          );
                world.playSound(
                        null,
                        playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(),
                        SoundEvents.ENTITY_GENERIC_EXPLODE,
                        SoundCategory.PLAYERS,
                        1.0F,
                        1.5F
                               );
            }
            
            // ===== 雷を発生させる =====
            for (int i = 0; i < count; i++) {
                double offsetX = (random.nextDouble() - 0.5) * HORIZONTAL_SPREAD;
                double offsetZ = (random.nextDouble() - 0.5) * HORIZONTAL_SPREAD;
                
                // 視線方向 + ランダムオフセット
                Vec3d rawTarget = origin.add(look.multiply(distance)).add(offsetX, 0, offsetZ);
                
                // === Y座標を地面に補正する ===
                BlockPos groundPos = serverWorld.getTopPosition(
                        net.minecraft.world.Heightmap.Type.MOTION_BLOCKING,
                        new BlockPos((int) rawTarget.x, (int) playerEntity.getY(), (int) rawTarget.z)
                                                               );
                Vec3d target = new Vec3d(rawTarget.x, groundPos.getY(), rawTarget.z);
                
                LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(serverWorld);
                if (lightning != null) {
                    lightning.refreshPositionAfterTeleport(target);
                    lightning.setCosmetic(false);
                    serverWorld.spawnEntity(lightning);
                }
            }
            
            
            // サウンド（通常）
            float pitch = 0.9F + (Math.min(pullProgress, 1.0F) * 0.2F); // 0.9～1.1
            world.playSound(
                    null,
                    playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(),
                    SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                    SoundCategory.PLAYERS,
                    2.0F,
                    pitch
                           );
            
            // 耐久値減少
            int damageAmount = (pullProgress > 1.0F) ? 2 : 1;
            if (random.nextInt(getUnbreakingLevel(stack) + 1) == 0) {
                stack.damage(damageAmount, playerEntity, p -> p.sendToolBreakStatus(playerEntity.getActiveHand()));
            }
            
            // 統計
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            
            // クールダウン
            int cooldown = (pullProgress > 1.0F) ? (COOLDOWN_TICKS * 2) : COOLDOWN_TICKS;
            playerEntity.getItemCooldownManager().set(this, cooldown);
            
            // ノックバック
            Vec3d knockback = look.multiply(-1).normalize().multiply(KNOCKBACK_STRENGTH);
            playerEntity.addVelocity(knockback.x, KNOCKBACK_Y, knockback.z);
            playerEntity.velocityModified = true;
        }
    }
    
    
    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000; // 弓と同じ最大チャージ時間
    }
    
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }
    
    public int getRange() {
        return 15; // 使っていないが互換で残す
    }
    
    //弓の引き具合
    public static float getPullProgress(int useTicks) {
        float f = (float) useTicks / 20.0F; // 20tick = 1秒
        f = (f * f + f * 2.0F) / 3.0F;
        return f; // Math.minを外す
    }
    
    
    //Unbreaking レベル取得
    private int getUnbreakingLevel(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.UNBREAKING, stack);
    }
    
    //貯め中のエフェクト
    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (!(entity instanceof PlayerEntity player))
            return;
        
        if (!world.isClient && player.isUsingItem() && player.getActiveItem().getItem() == this) {
            ServerWorld serverWorld = (ServerWorld) world;
            Vec3d pos = player.getPos().add(0, 1.0, 0); // 頭より少し低くする
            
            serverWorld.spawnParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    pos.x, pos.y, pos.z,
                    5, // 個数
                    0.3, 0.2, 0.3, // 範囲
                    0.01 // 速度
                                      );
        }
        
    }
}
