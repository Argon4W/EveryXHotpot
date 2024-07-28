package com.github.argon4w.hotpot.soups;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public interface IHotpotSoupTypeFactory<T extends IHotpotSoupType> {
    MapCodec<T> buildFromCodec(ResourceLocation resourceLocation);
    T buildFromScratch(ResourceLocation resourceLocation);
    IHotpotSoupFactorySerializer<T> getSerializer();
}
