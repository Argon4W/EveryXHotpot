package com.github.argon4w.hotpot.client.soups.effects;

import com.mojang.serialization.MapCodec;

public interface IHotpotSoupClientTickEffectSerializer<T extends IHotpotSoupClientTickEffect> {
    MapCodec<T> getCodec();
}
