package com.github.argon4w.hotpot.client.items.sprites.processors.providers;

import com.github.argon4w.hotpot.api.client.items.sprites.processors.providers.IHotpotSpriteProcessorProvider;
import com.github.argon4w.hotpot.client.items.sprites.processors.HotpotSpriteProcessors;
import com.github.argon4w.hotpot.api.items.sprites.IHotpotSpriteConfig;
import net.minecraft.resources.ResourceLocation;

public class HotpotEmptySpriteProcessorProvider implements IHotpotSpriteProcessorProvider {
    @Override
    public ResourceLocation getProcessorResourceLocation(IHotpotSpriteConfig config) {
        return HotpotSpriteProcessors.EMPTY_SPRITE_PROCESSOR_LOCATION;
    }
}
