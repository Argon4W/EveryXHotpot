package com.github.argon4w.hotpot.api.client.soups.renderers;

import com.mojang.serialization.MapCodec;

public interface IHotpotSoupCustomElementRendererSerializer<T extends IHotpotSoupCustomElementRenderer> {
    MapCodec<T> getCodec();
}
