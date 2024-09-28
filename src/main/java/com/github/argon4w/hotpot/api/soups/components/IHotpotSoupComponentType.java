package com.github.argon4w.hotpot.api.soups.components;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface IHotpotSoupComponentType<T extends IHotpotSoupComponent> {
    T get();
    MapCodec<T> getCodec();
    StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec();
    Holder<IHotpotSoupComponentTypeSerializer<?>> getSerializerHolder();
}
