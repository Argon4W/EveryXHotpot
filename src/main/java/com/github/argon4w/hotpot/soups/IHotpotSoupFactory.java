package com.github.argon4w.hotpot.soups;

import net.minecraft.resources.ResourceLocation;

public interface IHotpotSoupFactory<T extends IHotpotSoupType> {
    T build();
    IHotpotSoupTypeSerializer<T> getSerializer();
    ResourceLocation getResourceLocation();
}
