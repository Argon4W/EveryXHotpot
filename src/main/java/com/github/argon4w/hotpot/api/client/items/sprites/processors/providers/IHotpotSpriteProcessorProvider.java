package com.github.argon4w.hotpot.api.client.items.sprites.processors.providers;

import com.github.argon4w.hotpot.api.items.sprites.IHotpotSpriteConfig;
import net.minecraft.resources.ResourceLocation;

public interface IHotpotSpriteProcessorProvider {
    ResourceLocation getProcessorResourceLocation(IHotpotSpriteConfig config);
}
