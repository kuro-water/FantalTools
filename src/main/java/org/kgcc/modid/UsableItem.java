package org.kgcc.modid;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;

import static org.kgcc.modid.ExampleMod.MODID;

public class UsableItem extends Item {
    public UsableItem() {
        super(new Item.Settings().rarity(Rarity.COMMON));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.getWorld().isClient() && hand == Hand.MAIN_HAND) {
            user.sendMessage(Text.literal("Hello, Minecraft from Server!"));
            // duration（継続時間）: 20 ticks = 1 seconds
            // amplifier（強度）
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 20, 0));
        }
        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
//        if(entity instanceof PlayerEntity) {
//            return ActionResult.FAIL;
//        }
        if (user.getWorld().isClient()) {
            // クライアント側でのみ実行される処理
            user.sendMessage(Text.literal("Hello, Minecraft from Client!"));
        } else {
            // サーバー側でのみ実行される処理
            user.sendMessage(Text.literal("Hello, Minecraft from Server!"));
            if (hand == Hand.MAIN_HAND) {
                // メインハンドで右クリックされたときの処理
                if (entity.getType() == EntityType.SHEEP) {
                    SheepEntity sheep = (SheepEntity) entity;
                    sheep.setColor(DyeColor.RED);
                    if (!user.isCreative()) {
//                         クリエイティブでなければ、アイテムを1つ減らす
//
//                         引数に渡されるstackはコピーなので、これを変更しても元のアイテムは変更されない
//                        stack.decrement(1);
//                         そのため、プレイヤーの手に持っているアイテムを取得して減らす
                        user.getStackInHand(hand).decrement(1);
                    }
                } else {
                    // 羊でない場合
                    user.sendMessage(Text.literal("This is not a sheep!"));
                }
            }
        }
        return super.useOnEntity(stack, user, entity, hand);
    }

    // an instance of our new item
//    public static final Item testPickaxeItem = register(new Item(new Item.Settings().maxDamage(500)
//                                                                                    .rarity(Rarity.COMMON)),
//            "test_pickaxe");
//    public static final Item testPickaxeItem = register(new Item(new Item.Settings().maxDamage(500)
//                                                                                    .rarity(Rarity.COMMON)),
//            "test_pickaxe");

    public static Item register(Item instance, String path) {
        return Registry.register(Registries.ITEM, new Identifier(MODID, path), instance);
    }


    public static void initialize() {
    }
}
