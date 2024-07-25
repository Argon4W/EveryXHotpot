package com.github.argon4w.hotpot.soups;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface IHotpotSoupFactorySerializer<T extends IHotpotSoupType> {
    MapCodec<? extends IHotpotSoupFactory<T>> getCodec();
    StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupFactory<T>> getStreamCodec();
}
