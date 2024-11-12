package org.kgcc.fantalmod.armor;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class FantalArmorEffect {

    // 初期化メソッド。Modエントリーポイントで呼び出す必要があります。
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // サーバー上のすべてのプレイヤーに対して処理
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                applyResistanceEffect(player);
            }
        });
    }

    // 耐性バフを適用するメソッド
    private static void applyResistanceEffect(PlayerEntity player) {
        if (isWearingFullFantalArmor(player)) {
            // 耐性(Resistance)バフを付与（210ティック = 10.5秒）
            StatusEffectInstance resistance = new StatusEffectInstance(StatusEffects.RESISTANCE, 210, 0, false, false);
            if (!player.hasStatusEffect(StatusEffects.RESISTANCE)) {
                player.addStatusEffect(resistance);
            }
        } else {
            // 装備が足りない場合は効果を解除
            player.removeStatusEffect(StatusEffects.RESISTANCE);
        }
    }

    // プレイヤーがファンタル装備をフル装備しているかを確認する
    private static boolean isWearingFullFantalArmor(PlayerEntity player) {
        for (ItemStack armor : player.getArmorItems()) {
            if (!(armor.getItem() instanceof ArmorItem armorItem) ||
                    !armorItem.getMaterial().getName().equals("fantal")) {
                return false; // いずれかがファンタル装備でなければfalse
            }
        }
        return true; // 全てファンタル装備ならtrue
    }
}
