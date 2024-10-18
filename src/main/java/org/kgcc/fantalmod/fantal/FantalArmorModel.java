package org.kgcc.fantalmod.fantal;

import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.item.AmethystArmorItem;
import software.bernie.geckolib.model.GeoModel;
public class FantalArmorModel extends GeoModel<FantalArmorItem> {
    @Override
    public Identifier getModelResource(FantalArmorItem animatable) {
        return new Identifier(FantalMod.MODID, "geo/amethyst_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(FantalArmorItem animatable) {
        return new Identifier(FantalMod.MODID, "textures/armor/amethyst_armor.png");
    }

    @Override
    public Identifier getAnimationResource(FantalArmorItem animatable) {
        return new Identifier(FantalMod.MODID, "animations/amethyst_armor.animation.json");
    }
}
