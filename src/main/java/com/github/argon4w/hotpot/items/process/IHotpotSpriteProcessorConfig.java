package com.github.argon4w.hotpot.items.process;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

public interface IHotpotSpriteProcessorConfig {
    Holder<IHotpotSpriteProcessorConfigSerializer<?>> getSerializer();
    ResourceLocation getProcessorResourceLocation();
    ResourceLocation getResourceLocation();
}
