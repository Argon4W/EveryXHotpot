package com.github.argon4w.hotpot.items.process;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface IHotpotSpriteProcessorConfigSerializer<T extends IHotpotSpriteProcessorConfig> {
    MapCodec<T> getCodec();
    StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec();
}
