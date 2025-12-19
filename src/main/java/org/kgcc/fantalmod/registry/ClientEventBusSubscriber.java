package org.kgcc.fantalmod.registry;

import net.fabricmc.api.ClientModInitializer;
import org.kgcc.fantalmod.keybind.FantalKeyBind;
import org.kgcc.fantalmod.keybind.InputEvents;
import org.kgcc.fantalmod.util.ModModelPredicateProvider;

public class ClientEventBusSubscriber implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // キーバインドを登録
        FantalKeyBind.registerKeyBindings();
        InputEvents.registerKeyInput();




        ModModelPredicateProvider.registerModModels();
    }
}
