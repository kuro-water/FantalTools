package org.kgcc.fantalmod.tool;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;

public class FantalShieldItem extends ShieldItem {
    public FantalShieldItem() {
        super(new Settings().rarity(Rarity.COMMON).maxDamage(336)); // maxDamageはバニラの盾と同じ値
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            // ここでサーバー側の処理を実行しない
        }

        // 右クリックで盾のガードを発動させる
        user.setCurrentHand(hand); // プレイヤーが右クリックでガードしていることを示す
        return super.use(world, user, hand); // 親クラスのuseメソッドを呼び出して通常の盾の使用動作を維持
    }
}
