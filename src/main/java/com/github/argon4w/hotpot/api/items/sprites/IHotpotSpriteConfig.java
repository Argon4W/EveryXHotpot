package com.github.argon4w.hotpot.api.items.sprites;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

public interface IHotpotSpriteConfig {
    Holder<IHotpotSpriteConfigSerializer<?>> getSerializerHolder();
    ResourceLocation getResourceLocation();
}
