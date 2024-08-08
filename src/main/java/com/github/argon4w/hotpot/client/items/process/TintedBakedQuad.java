package com.github.argon4w.hotpot.client.items.process;

import com.github.argon4w.hotpot.client.HotpotColor;
import net.minecraft.client.renderer.block.model.BakedQuad;

public class TintedBakedQuad extends BakedQuad {
    private final HotpotColor color;

    public TintedBakedQuad(BakedQuad bakedQuad, HotpotColor color) {
        super(bakedQuad.getVertices(), bakedQuad.getTintIndex(), bakedQuad.getDirection(), bakedQuad.getSprite(), bakedQuad.isShade(), bakedQuad.hasAmbientOcclusion());
        this.color = color;
    }

    public HotpotColor getColor() {
        return color;
    }

    @Override
    public boolean isTinted() {
        return true;
    }
}
