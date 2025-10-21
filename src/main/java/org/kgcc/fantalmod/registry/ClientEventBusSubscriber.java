package org.kgcc.fantalmod.registry;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.keybind.FantalKeyBind;
import org.kgcc.fantalmod.keybind.InputEvents;
import org.kgcc.fantalmod.registry.FantalModItems;
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
