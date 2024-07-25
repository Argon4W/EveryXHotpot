package com.github.argon4w.hotpot.soups;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public record HotpotWrappedSoupTypeFactory<T extends IHotpotSoupType>(ResourceLocation resourceLocation, IHotpotSoupFactory<T> factory) implements IHotpotSoupFactory<T> {
    public MapCodec<T> buildFromCodec() {
        return factory.buildFromCodec(resourceLocation);
    }

    @Override
    public MapCodec<T> buildFromCodec(ResourceLocation resourceLocation) {
        return factory.buildFromCodec(resourceLocation);
    }

    @Override
    public T buildFromScratch(ResourceLocation resourceLocation) {
        return factory.buildFromScratch(resourceLocation);
    }

    @Override
    public IHotpotSoupFactorySerializer<T> getSerializer() {
        return factory.getSerializer();
    }
}
