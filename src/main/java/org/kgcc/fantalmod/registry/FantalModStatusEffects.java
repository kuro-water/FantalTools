package org.kgcc.fantalmod.registry;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.effect.NankaStatusEffect;

public class FantalModStatusEffects {
    public static final StatusEffect Nanka = new NankaStatusEffect();
    
    private static void register(StatusEffect effect, String path) {
        // Register the status effect with the game
        Registry.register(Registries.STATUS_EFFECT, new Identifier(FantalMod.MODID, path), effect);
    }
    
    public static void registerEffects() {
        FantalModStatusEffects.register(Nanka, "nanka");
    }
}