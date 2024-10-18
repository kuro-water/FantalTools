package org.kgcc.fantalmod.fantal;



import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class FantalArmorRenderer extends GeoArmorRenderer<FantalArmorItem> {
    public FantalArmorRenderer() {
        super(new FantalArmorModel());
    }
}
