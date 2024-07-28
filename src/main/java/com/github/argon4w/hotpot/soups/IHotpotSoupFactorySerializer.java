package com.github.argon4w.hotpot.soups;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface IHotpotSoupFactorySerializer<T extends IHotpotSoupType> {
    MapCodec<? extends IHotpotSoupTypeFactory<T>> getCodec();
    StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupTypeFactory<T>> getStreamCodec();
}
