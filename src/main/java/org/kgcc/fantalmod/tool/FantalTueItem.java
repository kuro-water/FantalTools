package org.kgcc.fantalmod.tool;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class FantalTueItem extends BowItem {
    public FantalTueItem() {
        super(new Settings().maxDamage(384)); // 弓の耐久値を設定
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            int usedTicks = this.getMaxUseTime(stack) - remainingUseTicks;
            float pullProgress = getPullProgress(usedTicks);

            // チャットメッセージ（Minecraft内）
            playerEntity.sendMessage(Text.literal("Used Ticks: " + usedTicks + ", Pull Progress: " + pullProgress), false);

            // Debugging the pull value and model switching condition
            if (pullProgress >= 0.9) {
                playerEntity.sendMessage(Text.literal("DEBUG - Pulling: Model 2 should be used."), false);
            } else if (pullProgress >= 0.65) {
                playerEntity.sendMessage(Text.literal("DEBUG - Pulling: Model 1 should be used."), false);
            } else {
                playerEntity.sendMessage(Text.literal("DEBUG - Pulling: Model 0 should be used."), false);
            }
        }



        if (user instanceof PlayerEntity playerEntity) {
            ItemStack itemStack = playerEntity.getProjectileType(stack);

            int i = this.getMaxUseTime(stack) - remainingUseTicks;
            float f = getPullProgress(i);

            if (!((double) f < 0.1)) {
                boolean infiniteArrows = playerEntity.getAbilities().creativeMode ||
                        EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0;

                if (!world.isClient) {
                    ArrowItem arrowItem = (ArrowItem) (itemStack.getItem() instanceof ArrowItem ? itemStack.getItem() : Items.ARROW);
                    PersistentProjectileEntity projectile = arrowItem.createArrow(world, itemStack, playerEntity);
                    projectile.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, f * 3.0F, 1.0F);

                    // **プレイヤーの視線の先にいるエンティティにダメージを与える**
                    Vec3d startPos = playerEntity.getCameraPosVec(1.0F);
                    Vec3d lookVec = playerEntity.getRotationVec(1.0F).multiply(30); // 30ブロック先まで判定
                    Vec3d endPos = startPos.add(lookVec);
                    HitResult hitResult = world.raycast(new RaycastContext(
                            startPos, endPos, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, playerEntity
                    ));

                    if (world instanceof ServerWorld serverWorld) {
                        MinecraftServer server = serverWorld.getServer();
                        server.getCommandManager().executeWithPrefix(
                                server.getCommandSource(),
                                "say 矢が発射された！"
                        );
                    }




                    // クリティカル判定
                    if (f == 1.0F) {
                        projectile.setCritical(true);
                    }

                    // エンチャント効果
                    int power = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
                    if (power > 0) {
                        projectile.setDamage(projectile.getDamage() + (double) power * 0.5 + 0.5);
                    }

                    int punch = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack);
                    if (punch > 0) {
                        projectile.setPunch(punch);
                    }

                    if (EnchantmentHelper.getLevel(Enchantments.FLAME, stack) > 0) {
                        projectile.setOnFireFor(100);
                    }

                    // 矢の消費処理
                    if (!infiniteArrows && !playerEntity.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                        if (itemStack.isEmpty()) {
                            playerEntity.getInventory().removeOne(itemStack);
                        }
                    }

                    // 弓の耐久値減少
                    stack.damage(1, playerEntity, (p) -> p.sendToolBreakStatus(playerEntity.getActiveHand()));

                    world.spawnEntity(projectile);
                }

                // 射撃音
                world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(),
                        SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F,
                        1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);

                playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        boolean hasArrows = !user.getProjectileType(itemStack).isEmpty();

        if (!user.getAbilities().creativeMode && !hasArrows) {
            return TypedActionResult.fail(itemStack);
        } else {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }
    }

    @Override
    public int getRange() {
        return 15;
    }

    /**
     * 弓の引き具合を計算するメソッド
     */
    public static float getPullProgress(int useTicks) {
        float f = (float) useTicks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        return Math.min(f, 1.0F);
    }
}
