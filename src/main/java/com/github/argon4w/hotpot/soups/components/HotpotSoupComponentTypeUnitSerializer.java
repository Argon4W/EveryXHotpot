package com.github.argon4w.hotpot.soups.components;

import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponent;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponentType;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponentTypeSerializer;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class HotpotSoupComponentTypeUnitSerializer<T extends IHotpotSoupComponent> implements IHotpotSoupComponentTypeSerializer<T> {
    private final MapCodec<? extends IHotpotSoupComponentType<T>> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupComponentType<T>> streamCodec;

    public HotpotSoupComponentTypeUnitSerializer(IHotpotSoupComponentType<T> componentType) {
        this.codec = MapCodec.unit(componentType);
        this.streamCodec = StreamCodec.unit(componentType);
    }

    public HotpotSoupComponentTypeUnitSerializer(T unit, Holder<IHotpotSoupComponentTypeSerializer<?>> serializerHolder) {
        this(new HotpotSoupComponentUnitType<>(unit, serializerHolder));
    }

    @Override
    public MapCodec<? extends IHotpotSoupComponentType<T>> getCodec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupComponentType<T>> getStreamCodec() {
        return streamCodec;
    }
}
