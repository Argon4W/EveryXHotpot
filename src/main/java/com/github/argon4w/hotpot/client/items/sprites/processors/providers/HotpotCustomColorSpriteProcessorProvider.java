package com.github.argon4w.hotpot.client.items.sprites.processors.providers;

import com.github.argon4w.hotpot.api.client.items.sprites.processors.providers.IHotpotSpriteProcessorProvider;
import com.github.argon4w.hotpot.client.items.sprites.processors.HotpotSpriteProcessors;
import com.github.argon4w.hotpot.items.sprites.HotpotCustomColorSpriteConfig;
import com.github.argon4w.hotpot.api.items.sprites.IHotpotSpriteConfig;
import net.minecraft.resources.ResourceLocation;

public class HotpotCustomColorSpriteProcessorProvider implements IHotpotSpriteProcessorProvider {
    @Override
    public ResourceLocation getProcessorResourceLocation(IHotpotSpriteConfig config) {
        return config instanceof HotpotCustomColorSpriteConfig customColor ? customColor.getProcessorResourceLocation() : HotpotSpriteProcessors.EMPTY_SPRITE_PROCESSOR_LOCATION;
    }
}
