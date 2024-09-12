package com.github.argon4w.hotpot.client.items.sprites.processors.providers;

import com.github.argon4w.hotpot.client.items.sprites.processors.HotpotSpriteProcessors;
import com.github.argon4w.hotpot.client.items.sprites.processors.IHotpotSpriteProcessor;
import com.github.argon4w.hotpot.items.sprites.HotpotCustomColorSpriteConfig;
import com.github.argon4w.hotpot.items.sprites.IHotpotSpriteConfig;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

public class HotpotCustomColorSpriteProcessorProvider implements IHotpotSpriteProcessorProvider {
    @Override
    public ResourceLocation getProcessorResourceLocation(IHotpotSpriteConfig config) {
        return config instanceof HotpotCustomColorSpriteConfig customColor ? customColor.getProcessorResourceLocation() : HotpotSpriteProcessors.EMPTY_SPRITE_PROCESSOR_LOCATION;
    }
}
