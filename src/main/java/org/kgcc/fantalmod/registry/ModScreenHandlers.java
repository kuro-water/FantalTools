package org.kgcc.fantalmod.registry;


import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import org.kgcc.fantalmod.screen.FantalBenchScreenHandler;

public class ModScreenHandlers {
    public static ScreenHandlerType<FantalBenchScreenHandler> FANTAL_BENCH_SCREEN_HANDLER;
    public static void registerAllScreenHandlers() {
        FANTAL_BENCH_SCREEN_HANDLER = new ScreenHandlerType<>(FantalBenchScreenHandler::new, FeatureSet.empty());
    }
}
