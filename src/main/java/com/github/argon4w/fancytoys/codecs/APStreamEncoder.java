package com.github.argon4w.fancytoys.codecs;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamEncoder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class APStreamEncoder<A, R> implements StreamEncoder<RegistryFriendlyByteBuf, R> {

    private final A value;
    private final StreamEncoder<RegistryFriendlyByteBuf, A> aEncoder;
    private final StreamEncoder<RegistryFriendlyByteBuf, Function<A, R>> fEncoder;

    public APStreamEncoder(A value, UniMapCodec.Encoder<A> aEncoder, UniMapCodec.Encoder<Function<A, R>> fEncoder) {
        this.value = value;
        this.aEncoder = aEncoder.streamEncoder();
        this.fEncoder = fEncoder.streamEncoder();
    }

    @Override
    public void encode(@NotNull RegistryFriendlyByteBuf buffer, @NotNull R value) {
        aEncoder.encode(buffer, this.value);
        fEncoder.encode(buffer, a -> value);
    }
}
