package com.github.argon4w.fancytoys.codecs;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class BuilderStreamCodec<O> implements StreamCodec<RegistryFriendlyByteBuf, O> {

    private final UniRecordCodec<O, O> builder;

    public BuilderStreamCodec(UniRecordCodec<O, O> builder) {
        this.builder = builder;
    }

    @Override
    public void encode(@NotNull RegistryFriendlyByteBuf buffer, @NotNull O value) {
        builder.getEncoder().apply(value).streamEncoder().encode(buffer, value);
    }

    @NotNull
    @Override
    public O decode(@NotNull RegistryFriendlyByteBuf buffer) {
        return builder.getDecoder().streamDecoder().decode(buffer);
    }
}
