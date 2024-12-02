package org.kgcc.fantalmod.village;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerProfessionBuilder;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.registry.FantalModItems;

// /summon minecraft:villager ~ ~ ~ {VillagerData:{profession:"fantalmod:jumpmaster"}}

public class FantalModVillagers {
    public static final PointOfInterestType FANTALSMITH_POI = registerPOI("fantalsmith", FantalModItems.FANTAL_BLOCK);
    public static final VillagerProfession FANTALSMITH = registerProfession("fantalsmith", RegistryKey.of(
            RegistryKeys.POINT_OF_INTEREST_TYPE, new Identifier(FantalMod.MODID, "fantalsmith")));
    
    public static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> type) {
        return Registry.register(Registries.VILLAGER_PROFESSION, new Identifier(FantalMod.MODID, name),
                                 VillagerProfessionBuilder.create()
                                                          .id(new Identifier(FantalMod.MODID, name))
                                                          .workstation(type)
                                                          .workSound(SoundEvents.ENTITY_VILLAGER_WORK_ARMORER)
                                                          .build());
    }
    
    public static PointOfInterestType registerPOI(String name, Block block) {
        return PointOfInterestHelper.register(new Identifier(FantalMod.MODID, name), 1, 1,
                                              ImmutableSet.copyOf(block.getStateManager().getStates()));
    }
    
    public static void registerVillagers() {
        FantalMod.LOGGER.debug("Registering Villagers for " + FantalMod.MODID);
    }
    
    public static void registerTrades() {
        TradeOfferHelper.registerVillagerOffers(FANTALSMITH, 1, factories -> factories.add(
                ((entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, 3),
                                                    new ItemStack(FantalModItems.FANTAL_INGOT, 5), 6, 2,
                                                    0.02f))));
    }
}
