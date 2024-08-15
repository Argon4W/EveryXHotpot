package com.github.argon4w.hotpot.client.items.sprites.processors;

public class HotpotLightSaucedSpriteProcessor extends AbstractHotpotHalfSaucedSpriteProcessor {
    @Override
    public float getAlphaModifier() {
        return 0.7f;
    }

    @Override
    public String getSuffix() {
        return "_light_sauced";
    }
}
