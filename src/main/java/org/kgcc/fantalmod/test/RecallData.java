package org.kgcc.fantalmod.test;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.kgcc.fantalmod.util.NbtUtils;

/**
 * リコール用のデータをテレポート一回分保存するクラス
 * 記録はLinkedListで行うことを想定
 */
public class RecallData {
    private final String dimensionKey;
    public final Vec3d pos;
    public final Vec3d velocity;
    // yaw：水平角度（左右）
    public final Float yaw;
    // pitch：垂直角度（上下）
    public final Float pitch;
    public final Float health;
    
    public RecallData(String dimensionKey, Vec3d pos, Vec3d velocity, Float yaw, Float pitch, Float health) {
        this.dimensionKey = dimensionKey;
        this.velocity = velocity;
        this.pos = pos;
        this.yaw = yaw;
        this.pitch = pitch;
        this.health = health;
    }
    
    public RecallData(RegistryKey<World> dimension, Vec3d pos, Vec3d velocity, Float yaw, Float pitch, Float health) {
        this(dimension.getValue().toString(), pos, velocity, yaw, pitch, health);
    }
    
    public RecallData(PlayerEntity playerEntity) {
        this(
                playerEntity.getWorld().getRegistryKey(),
                playerEntity.getPos(),
                playerEntity.getVelocity(),
                playerEntity.getYaw(),
                playerEntity.getPitch(),
                playerEntity.getHealth()
            );
    }
    
    public RegistryKey<World> getDimensionKey() {
        return RegistryKey.of(RegistryKeys.WORLD, new Identifier(this.dimensionKey));
    }
    
    public ServerWorld getWorld(MinecraftServer server) {
        return server.getWorld(RegistryKey.of(RegistryKeys.WORLD, new Identifier(this.dimensionKey)));
    }
    
    // NBTへの変換
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("dimensionKey", dimensionKey);
        nbt.put("pos", NbtUtils.vec3dToNbt(pos));
        nbt.put("velocity", NbtUtils.vec3dToNbt(velocity));
        nbt.putFloat("yaw", yaw);
        nbt.putFloat("pitch", pitch);
        nbt.putFloat("health", health);
        return nbt;
    }
    
    // NBTからの復元
    public static RecallData fromNbt(NbtCompound nbt) {
        String dimensionKey = nbt.getString("dimensionKey");
        Vec3d pos = NbtUtils.nbtToVec3d(nbt.getCompound("pos"));
        Vec3d velocity = NbtUtils.nbtToVec3d(nbt.getCompound("velocity"));
        float yaw = nbt.getFloat("yaw");
        float pitch = nbt.getFloat("pitch");
        float health = nbt.getFloat("health");
        return new RecallData(dimensionKey, pos, velocity, yaw, pitch, health);
    }
    
    // Packetへの変換
    public void toPacket(PacketByteBuf buf) {
        buf.writeString(dimensionKey);
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);
        buf.writeDouble(velocity.x);
        buf.writeDouble(velocity.y);
        buf.writeDouble(velocity.z);
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        buf.writeFloat(health);
    }
    
    // Packetからの復元
    public static RecallData fromPacket(PacketByteBuf buf) {
        String dimensionKey = buf.readString();
        Vec3d pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        Vec3d velocity = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        float yaw = buf.readFloat();
        float pitch = buf.readFloat();
        float health = buf.readFloat();
        return new RecallData(dimensionKey, pos, velocity, yaw, pitch, health);
    }
}
