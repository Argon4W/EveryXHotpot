package com.github.argon4w.fancytoys.codecs;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamDecoder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class APStreamDecoder<A, R> implements StreamDecoder<RegistryFriendlyByteBuf, R> {

    private final StreamDecoder<RegistryFriendlyByteBuf, A> aDecoder;
    private final StreamDecoder<RegistryFriendlyByteBuf, Function<A, R>> fDecoder;

    public APStreamDecoder(UniMapCodec.Decoder<A> aDecoder, UniMapCodec.Decoder<Function<A, R>> fDecoder) {
        this.aDecoder = aDecoder.streamDecoder();
        this.fDecoder = fDecoder.streamDecoder();
    }

    @NotNull
    @Override
    public R decode(@NotNull RegistryFriendlyByteBuf buffer) {
        A a = aDecoder.decode(buffer);
        return fDecoder.decode(buffer).apply(a);
    }
}
