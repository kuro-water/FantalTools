package org.kgcc.fantalmod.item;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.FantalMod;
import software.bernie.geckolib.model.GeoModel;
public class AmethystArmorModel extends GeoModel<AmethystArmorItem> {
    @Override
    public Identifier getModelResource(AmethystArmorItem animatable) {
        return new Identifier(FantalMod.MODID, "geo/amethyst_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(AmethystArmorItem animatable) {
        return new Identifier(FantalMod.MODID, "textures/armor/amethyst_armor.png");
    }

    @Override
    public Identifier getAnimationResource(AmethystArmorItem animatable) {
        return new Identifier(FantalMod.MODID, "animations/amethyst_armor.animation.json");
    }
}