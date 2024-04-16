package com.github.argon4w.hotpot.client.items.process.processors;

import com.github.argon4w.hotpot.HotpotModEntry;
import net.minecraft.resources.ResourceLocation;

public class HotpotLightSaucedSpriteProcessor extends AbstractHotpotHalfSaucedSpriteProcessor {
    @Override
    public float getAlphaModifier() {
        return 0.7f;
    }

    @Override
    public String getProcessedSuffix() {
        return "_light_sauced";
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(HotpotModEntry.MODID, "light_sauced_processor");
    }

    @Override
    public int getIndex() {
        return 1;
    }
}
