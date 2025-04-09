package org.kgcc.fantalmod.registry;


import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import org.kgcc.fantalmod.gui.GemInfusingScreenHandler;
import org.kgcc.fantalmod.fantalgui.SiroanBlockScreenHandler;

public class ModScreenHandlers {
    public static ScreenHandlerType<GemInfusingScreenHandler> GEM_INFUSING_SCREEN_HANDLER;
    public static ScreenHandlerType<SiroanBlockScreenHandler> SIROAN_BLOCK_SCREEN_HANDLER;
    public static void registerAllScreenHandlers() {
        GEM_INFUSING_SCREEN_HANDLER = new ScreenHandlerType<>(GemInfusingScreenHandler::new, FeatureSet.empty());
        SIROAN_BLOCK_SCREEN_HANDLER = new ScreenHandlerType<>(SiroanBlockScreenHandler::new, FeatureSet.empty());
    }
}
