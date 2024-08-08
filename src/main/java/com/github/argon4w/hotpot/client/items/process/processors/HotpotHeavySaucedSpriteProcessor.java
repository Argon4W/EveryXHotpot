package com.github.argon4w.hotpot.client.items.process.processors;

public class HotpotHeavySaucedSpriteProcessor extends AbstractHotpotHalfSaucedSpriteProcessor {
    @Override
    public float getAlphaModifier() {
        return 1.0f;
    }

    @Override
    public String getProcessedSuffix() {
        return "_heavy_sauced";
    }
}
