package org.kgcc.fantalmod.registry;


import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import org.kgcc.fantalmod.gui.SiroanBlockScreenHandler;

public class ModScreenHandlers {
    public static ScreenHandlerType<SiroanBlockScreenHandler> SIROAN_BLOCK_SCREEN_HANDLER;
    public static void registerAllScreenHandlers() {
        SIROAN_BLOCK_SCREEN_HANDLER = new ScreenHandlerType<>(SiroanBlockScreenHandler::new, FeatureSet.empty());
    }
}
