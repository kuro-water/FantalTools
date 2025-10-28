package org.kgcc.fantalmod.registry;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.entity.FantalBenchEntity;
import org.kgcc.fantalmod.screen.FantalBenchScreenHandler;

public class FantalModScreenHandlers {
    public static ScreenHandlerType<FantalBenchScreenHandler> FANTAL_BENCH_SCREEN_HANDLER;
    
    public static void initialize() {
        FANTAL_BENCH_SCREEN_HANDLER = Registry.register(
                Registries.SCREEN_HANDLER,
                new Identifier(FantalMod.MODID, "fantal_bench"),
                new ExtendedScreenHandlerType<>((syncId, inv, buf) -> {
                    FantalBenchEntity blockEntity = (FantalBenchEntity) inv.player.getWorld()
                            .getBlockEntity(buf.readBlockPos());
                    return new FantalBenchScreenHandler(syncId, inv, blockEntity);
                })
        );
    }
}
