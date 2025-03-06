package org.kgcc.fantalmod.gui;


import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandlers {
    public static ScreenHandlerType<GemInfusingScreenHandler> GEM_INFUSING_SCREEN_HANDLER;

    public static void registerAllScreenHandlers() {
        GEM_INFUSING_SCREEN_HANDLER = new ScreenHandlerType<>(GemInfusingScreenHandler::new, FeatureSet.empty());

    }
}
