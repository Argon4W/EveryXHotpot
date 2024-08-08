package com.github.argon4w.hotpot.client.items.process.processors;

public class HotpotLightSaucedSpriteProcessor extends AbstractHotpotHalfSaucedSpriteProcessor {
    @Override
    public float getAlphaModifier() {
        return 0.7f;
    }

    @Override
    public String getProcessedSuffix() {
        return "_light_sauced";
    }
}
