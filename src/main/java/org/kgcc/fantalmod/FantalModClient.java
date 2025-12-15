package org.kgcc.fantalmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.client.render.FantalArrowEntityRenderer;
import org.kgcc.fantalmod.init.ModEntities;
import org.kgcc.fantalmod.keybind.FantalKeyBind;
import org.kgcc.fantalmod.registry.FantalModItems;
import org.kgcc.fantalmod.registry.FantalModScreenHandlers;
import org.kgcc.fantalmod.screen.FantalBenchScreen;

import java.util.List;

@Environment(EnvType.CLIENT)
public class FantalModClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(TestBlock.FANTAL_CRYSTAL, RenderLayer.getCutout());
        
        // サーバーからのpollutionデータを受信し、FantalModStateに保存
        ClientPlayNetworking.registerGlobalReceiver(
                FantalMod.FANTAL_POLLUTION,
                (client, handler, buf, responseSender) -> {
                    int totalFantalPollution = buf.readInt();
                    int playerFantalPollution = buf.readInt();
                    
                    // クライアント側のFantalModStateに保存
                    client.execute(() -> {
                        FantalModState.setTotalPollution(totalFantalPollution);
                        FantalModState.setPlayerPollution(playerFantalPollution);
                        
//                        FantalMod.LOGGER.info("Received pollution data - Total: {}, Player: {}",
//                                totalFantalPollution, playerFantalPollution);
                    });
                });
        
        HandledScreens.register(FantalModScreenHandlers.FANTAL_BENCH_SCREEN_HANDLER, FantalBenchScreen::new);
        // ModelPredicateの設定を一つの関数でまとめる
        registerModelPredicates();
        
        // キーバインドの登録
        FantalKeyBind.registerKeyBindings();


        EntityRendererRegistry.register(ModEntities.FANTAL_ARROW_ENTITY, FantalArrowEntityRenderer::new);

        ScreenEvents.AFTER_INIT.register(((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof GameMenuScreen))
                return;

            List<ClickableWidget> widgets = net.fabricmc.fabric.api.client.screen.v1.Screens.getButtons(screen);
            widgets.forEach(widget -> {
                FantalMod.LOGGER.info("{}", widget.getClass().getSimpleName());
            });


            ButtonWidget fantalmodsettingBtn = ButtonWidget.builder(Text.of("Fantal Mod"), (widget) -> {
                if (client.player != null)
                    client.setScreen(new ConfigButtonScreen(screen));
            }).dimensions(screen.width / 2 - 102, screen.height / 4 + 128, 204, 20).build();
            widgets.add(fantalmodsettingBtn);
        }));
    }
    
    private void registerModelPredicates() {
        // 弓
        registerBowPredicates();
        
        // 杖
        registerWandPredicates();
    }
    
    private void registerBowPredicates() {
        ModelPredicateProviderRegistry.register(
                FantalModItems.FANTAL_BOW, new Identifier("pulling"),
                (stack, world, entity, seed) -> (entity != null && entity.isUsingItem() && entity.getActiveItem() == stack) ? 1.0F : 0.0F
                                               );
        
        ModelPredicateProviderRegistry.register(
                FantalModItems.FANTAL_BOW, new Identifier("pull"),
                (stack, world, entity, seed) -> {
                    if (entity == null)
                        return 0.0F;
                    return entity.getActiveItem() != stack ? 0.0F :
                            (float) (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
                }
                                               );
    }
    
    private void registerWandPredicates() {
        ModelPredicateProviderRegistry.register(
                FantalModItems.FANTAL_WAND, new Identifier("pulling"),
                (stack, world, entity, seed) -> (entity != null && entity.isUsingItem() && entity.getActiveItem() == stack) ? 1.0F : 0.0F
                                               );
        
        ModelPredicateProviderRegistry.register(
                FantalModItems.FANTAL_WAND, new Identifier("pull"),
                (stack, world, entity, seed) -> {
                    if (entity == null)
                        return 0.0F;
                    return entity.getActiveItem() != stack ? 0.0F :
                            (float) (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
                }
                                               );
    }
    
}
