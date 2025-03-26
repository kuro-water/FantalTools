package org.kgcc.fantalmod.tool;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.kgcc.fantalmod.util.FantalStateManager;

public class FantalCrossbowItem extends CrossbowItem {
    public FantalCrossbowItem() {
        super(new Settings().rarity(Rarity.COMMON));
    }

    // 使用開始（クロスボウを引き始める）
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (isCharged(itemStack)) {
            // すでに装填済みなら発射処理へ
            fireCrossbow(world, user, hand, itemStack);
            return TypedActionResult.pass(itemStack);
        } else {
            // 引き始める
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }
    }

    // 引き終わった（クロスボウを放す）
    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity player) {
            int usedTicks = this.getMaxUseTime(stack) - remainingUseTicks;
            float pullProgress = (float) usedTicks / (float) this.getMaxUseTime(stack);

            // ある程度引いたら装填（バニラのロジックを参考）
            if (pullProgress >= 1.0f) {
                stack.getOrCreateNbt().putBoolean("Charged", true);
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ITEM_CROSSBOW_LOADING_END, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
        }
    }

    // 発射処理
    private void fireCrossbow(World world, PlayerEntity user, Hand hand, ItemStack stack) {
        if (!world.isClient) {
            // 効果を適用（攻撃力上昇）
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * FantalStateManager.TICK_PAR_SEC, 1));

            // Fantal汚染を増加
            var server = world.getServer();
            FantalStateManager.addFantalPollution(server, user, 1);
            FantalStateManager.sendFantalPollution(server, user);
        }

        // 装填解除
        stack.getOrCreateNbt().putBoolean("Charged", false);
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    // クロスボウの「装填済み」状態を取得
    public static boolean isCharged(ItemStack stack) {
        return stack.hasNbt() && stack.getNbt().getBoolean("Charged");
    }
}
