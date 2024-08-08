package com.github.argon4w.hotpot.soups;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;

public interface IHotpotSoupType<T extends IHotpotSoup> {
    MapCodec<T> getCodec(HotpotSoupTypeHolder<T> soupTypeFactoryHolder);
    T getSoup(HotpotSoupTypeHolder<T> soupTypeFactoryHolder);
    Holder<IHotpotSoupTypeSerializer<?>> getSerializer();
}
