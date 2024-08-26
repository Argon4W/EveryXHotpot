package com.github.argon4w.hotpot.soups.components;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class HotpotSoupComponentUnitType<T extends IHotpotSoupComponent> implements IHotpotSoupComponentType<T> {
    private final T unit;
    private final Holder<IHotpotSoupComponentTypeSerializer<?>> serializerHolder;

    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public HotpotSoupComponentUnitType(T unit, Holder<IHotpotSoupComponentTypeSerializer<?>> serializerHolder) {
        this.unit = unit;
        this.serializerHolder = serializerHolder;

        this.codec = MapCodec.unit(unit);
        this.streamCodec = StreamCodec.unit(unit);
    }

    @Override
    public MapCodec<T> getCodec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec() {
        return streamCodec;
    }

    @Override
    public T get() {
        return unit;
    }

    @Override
    public Holder<IHotpotSoupComponentTypeSerializer<?>> getSerializerHolder() {
        return serializerHolder;
    }
}
