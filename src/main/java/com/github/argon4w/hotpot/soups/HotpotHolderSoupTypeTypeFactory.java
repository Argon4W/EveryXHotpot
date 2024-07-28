package com.github.argon4w.hotpot.soups;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

public record HotpotHolderSoupTypeTypeFactory<T extends IHotpotSoupType>(Holder<IHotpotSoupTypeFactory<T>> holder) implements IHotpotSoupTypeFactory<T> {
    public MapCodec<T> buildFromCodec() {
        return holder.value().buildFromCodec(holder.getKey().location());
    }

    public T buildFromScratch() {
        return holder.value().buildFromScratch(holder.getKey().location());
    }

    @Override
    public MapCodec<T> buildFromCodec(ResourceLocation resourceLocation) {
        return holder.value().buildFromCodec(resourceLocation);
    }

    @Override
    public T buildFromScratch(ResourceLocation resourceLocation) {
        return holder.value().buildFromScratch(resourceLocation);
    }

    @Override
    public IHotpotSoupFactorySerializer<T> getSerializer() {
        return holder.value().getSerializer();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotHolderSoupTypeTypeFactory<?> holderFactory && holderFactory.holder.equals(holder);
    }
}