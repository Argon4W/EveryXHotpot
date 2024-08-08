package com.github.argon4w.hotpot.soups;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public record HotpotSoupTypeHolder<T extends IHotpotSoup>(ResourceLocation key, IHotpotSoupType<T> value) {
    public MapCodec<T> getCodec() {
        return value.getCodec(this);
    }

    public T getSoup() {
        return value.getSoup(this);
    }

    public boolean equals(IHotpotSoup soupType) {
        return key.equals(soupType.getSoupTypeHolder().key);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotSoupTypeHolder<?> holder && key.equals(holder.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
