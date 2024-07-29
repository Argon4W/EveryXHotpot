package com.github.argon4w.hotpot.soups;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public record HotpotSoupTypeFactoryHolder<T extends IHotpotSoupType>(ResourceLocation key, IHotpotSoupTypeFactory<T> value) {
    public MapCodec<T> buildFromCodec() {
        return value.buildFromCodec(this);
    }

    public T buildFromScratch() {
        return value.buildFromScratch(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotSoupTypeFactoryHolder<?> holder && key.equals(holder.key);
    }
}
