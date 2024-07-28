package com.github.argon4w.hotpot.soups;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public record HotpotWrappedSoupTypeTypeFactory<T extends IHotpotSoupType>(ResourceLocation resourceLocation, IHotpotSoupTypeFactory<T> factory) implements IHotpotSoupTypeFactory<T> {
    public MapCodec<T> buildFromCodec() {
        return factory.buildFromCodec(resourceLocation);
    }

    public T buildFromScratch() {
        return factory.buildFromScratch(resourceLocation);
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotWrappedSoupTypeTypeFactory<?> wrappedFactory && resourceLocation.equals(wrappedFactory.resourceLocation);
    }
}