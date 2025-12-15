package org.kgcc.fantalmod.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

public class NbtUtils {
    // Vec3dをNBTに変換
    public static NbtCompound vec3dToNbt(Vec3d vec) {
        NbtCompound nbt = new NbtCompound();
        nbt.putDouble("x", vec.x);
        nbt.putDouble("y", vec.y);
        nbt.putDouble("z", vec.z);
        return nbt;
    }
    
    // NBTからVec3dを復元
    public static Vec3d nbtToVec3d(NbtCompound nbt) {
        return new Vec3d(
                nbt.getDouble("x"),
                nbt.getDouble("y"),
                nbt.getDouble("z")
        );
    }
}
