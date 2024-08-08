package com.github.argon4w.hotpot.soups;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface IHotpotSoupTypeSerializer<T extends IHotpotSoup> {
    MapCodec<? extends IHotpotSoupType<T>> getCodec();
    StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupType<T>> getStreamCodec();
}
