package org.kgcc.fantalmod.client.render;

import net.minecraft.client.render.entity.EntityRendererFactory;
// ↓ 継承元をこれに変更します
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;
import org.kgcc.fantalmod.FantalMod;
import org.kgcc.fantalmod.entity.projectile.FantalArrowEntity;


public class FantalArrowEntityRenderer extends ProjectileEntityRenderer<FantalArrowEntity> {

    public static final Identifier TEXTURE = new Identifier(FantalMod.MODID, "textures/entity/fantal_arrow.png");

    public FantalArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(FantalArrowEntity entity) {
        return TEXTURE;
    }
}