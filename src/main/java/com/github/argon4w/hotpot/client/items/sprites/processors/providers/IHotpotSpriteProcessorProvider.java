package com.github.argon4w.hotpot.client.items.sprites.processors.providers;

import com.github.argon4w.hotpot.items.sprites.IHotpotSpriteConfig;
import net.minecraft.resources.ResourceLocation;

public interface IHotpotSpriteProcessorProvider {
    ResourceLocation getProcessorResourceLocation(IHotpotSpriteConfig config);
}
