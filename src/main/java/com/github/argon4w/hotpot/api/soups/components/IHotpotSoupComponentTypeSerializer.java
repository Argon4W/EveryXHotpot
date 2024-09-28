package com.github.argon4w.hotpot.api.soups.components;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface IHotpotSoupComponentTypeSerializer<T extends IHotpotSoupComponent> {
    MapCodec<? extends IHotpotSoupComponentType<T>> getCodec();
    StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupComponentType<T>> getStreamCodec();
}
