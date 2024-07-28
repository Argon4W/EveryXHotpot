package com.github.argon4w.hotpot.client.items.process.processors;

import com.github.argon4w.hotpot.HotpotModEntry;
import net.minecraft.resources.ResourceLocation;

public class HotpotHeavySaucedSpriteProcessor extends AbstractHotpotHalfSaucedSpriteProcessor {
    @Override
    public float getAlphaModifier() {
        return 1.0f;
    }

    @Override
    public String getProcessedSuffix() {
        return "_heavy_sauced";
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "heavy_sauced_processor");
    }

    @Override
    public int getIndex() {
        return 0;
    }
}
