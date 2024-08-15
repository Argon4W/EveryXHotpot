package com.github.argon4w.hotpot.items.sprites;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

public interface IHotpotSpriteConfig {
    Holder<IHotpotSpriteConfigSerializer<?>> getSerializerHolder();
    ResourceLocation getProcessorResourceLocation();
    ResourceLocation getResourceLocation();
}
