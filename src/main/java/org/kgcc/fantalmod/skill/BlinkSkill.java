package org.kgcc.fantalmod.skill;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.kgcc.fantalmod.FantalMod;

public class BlinkSkill implements BaseSkill {
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        
        var yaw = Math.toRadians(user.getYaw() * -1); // 角度と方向が合ってないので補正
        var pitch = Math.toRadians(user.getPitch() * -1); // 下を向いてるとマイナスになるように補正
        
        var y = Math.sin(pitch);
        var x = Math.sin(yaw) * (1 - y);
        var z = Math.cos(yaw) * (1 - y);
        
//        FantalMod.LOGGER.info("x:{} y:{} z:{}", x, y, z);
//        FantalMod.LOGGER.info("yaw:{} pitch:{}", yaw, pitch);
        
        // deltaY = y*1.75/2だと、真上使用3ブロック、ジャンプ+真上使用で最大9ブロックほど飛べる
        // deltaY = y*1.5/2だと、真上使用2.5ブロック（ギリダメージ受けない）、ジャンプ+真上使用で最大8ブロックほど飛べる
        var speed = 1.5;
        user.addVelocity(x * speed, y * speed / 2, z * speed);
        user.velocityModified = true;
        
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
