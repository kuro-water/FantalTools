package org.kgcc.fantalmod.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.kgcc.fantalmod.entity.projectile.FantalArrowEntity;

import static org.kgcc.fantalmod.tool.FantalWandItem.getPullProgress;

public class FantalArrowItem extends ArrowItem {
    public FantalArrowItem(Settings settings) {
        super(settings.rarity(Rarity.UNCOMMON));  // レアリティなど設定
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity playerEntity)) return;

        int usedTicks = this.getMaxUseTime(stack) - remainingUseTicks;
        float pullProgress = getPullProgress(usedTicks);

        if (pullProgress < 0.1F) return;

        if (!world.isClient) {
            for (int j = 0; j < 15; j++) {
                FantalArrowEntity projectile = new FantalArrowEntity(world, playerEntity);

                float yaw = playerEntity.getYaw() + (world.getRandom().nextFloat() - 0.5f) * 10.0f;
                float pitch = playerEntity.getPitch() + (world.getRandom().nextFloat() - 0.5f) * 5.0f;

                projectile.setVelocity(playerEntity, pitch, yaw, 0.0F, pullProgress * 3.0F, 1.0F);

                if (pullProgress == 1.0F) {
                    projectile.setCritical(true);
                }

                // 各種エンチャント効果（必要に応じて）
                int power = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
                if (power > 0) {
                    projectile.setDamage(projectile.getDamage() + power * 0.5 + 0.5);
                }

                world.spawnEntity(projectile);
            }

            stack.damage(1, playerEntity, (p) -> p.sendToolBreakStatus(playerEntity.getActiveHand()));
        }

        world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(),
                SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F,
                1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + pullProgress * 0.5F);

        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
    }

}
