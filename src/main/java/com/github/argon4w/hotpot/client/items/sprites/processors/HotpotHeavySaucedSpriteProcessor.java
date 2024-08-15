package com.github.argon4w.hotpot.client.items.sprites.processors;

public class HotpotHeavySaucedSpriteProcessor extends AbstractHotpotHalfSaucedSpriteProcessor {
    @Override
    public float getAlphaModifier() {
        return 1.0f;
    }

    @Override
    public String getSuffix() {
        return "_heavy_sauced";
    }
}
