package com.github.argon4w.hotpot.soups;

import com.mojang.serialization.MapCodec;

public interface IHotpotSoupTypeFactory<T extends IHotpotSoupType> {
    MapCodec<T> buildFromCodec(HotpotSoupTypeFactoryHolder<T> soupTypeFactoryHolder);
    T buildFromScratch(HotpotSoupTypeFactoryHolder<T> soupTypeFactoryHolder);
    IHotpotSoupFactorySerializer<T> getSerializer();
}
