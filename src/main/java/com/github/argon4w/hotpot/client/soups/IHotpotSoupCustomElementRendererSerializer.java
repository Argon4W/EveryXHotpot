package com.github.argon4w.hotpot.client.soups;

import com.mojang.serialization.MapCodec;

public interface IHotpotSoupCustomElementRendererSerializer<T extends IHotpotSoupCustomElementRenderer> {
    MapCodec<T> getCodec();
}
