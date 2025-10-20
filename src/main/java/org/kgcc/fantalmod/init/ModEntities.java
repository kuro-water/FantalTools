package org.kgcc.fantalmod.init;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.entity.projectile.FantalArrowEntity;
import org.kgcc.fantalmod.entity.projectile.FantalSnowballEntity;

public class ModEntities {
    public static final EntityType<FantalArrowEntity> FANTAL_ARROW_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(FantalMod.MODID, "fantal_arrow"),
            FabricEntityTypeBuilder.<FantalArrowEntity>create(SpawnGroup.MISC, FantalArrowEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .trackRangeBlocks(4)
                    .trackedUpdateRate(20)
                    .build()
    );

    public static final EntityType<FantalSnowballEntity> FANTAL_SNOWBALL_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(FantalMod.MODID, "fantal_snowball"),
            FabricEntityTypeBuilder.<FantalSnowballEntity>create(SpawnGroup.MISC, FantalSnowballEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f))  // 雪玉のサイズ
                    .trackRangeBlocks(4)
                    .trackedUpdateRate(10)
                    .build()
    );

    public static void register() {
        // 呼び出しだけでOK（空で良い）
    }
}
