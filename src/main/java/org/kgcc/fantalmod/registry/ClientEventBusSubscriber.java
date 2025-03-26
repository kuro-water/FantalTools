package org.kgcc.fantalmod.registry;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.keybind.FantalKeyBind;
import org.kgcc.fantalmod.keybind.InputEvents;
import org.kgcc.fantalmod.registry.FantalModItems;

public class ClientEventBusSubscriber implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // キーバインドを登録
        FantalKeyBind.registerKeyBindings();
        InputEvents.registerKeyInput();

        // 弓のカスタムモデルプロパティ（引き具合）
        ModelPredicateProviderRegistry.register(FantalModItems.FANTAL_BOW, new Identifier("pull"),
                (stack, world, entity, seed) -> {
                    if (entity == null) {
                        return 0.0F;
                    } else {
                        return entity.getActiveItem() != stack ? 0.0F :
                                (float) (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20F;
                    }
                });

        // 弓のカスタムモデルプロパティ（引いているか）
        ModelPredicateProviderRegistry.register(FantalModItems.FANTAL_BOW, new Identifier("pulling"),
                (stack, world, entity, seed) ->
                        entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
        );
    }
}
