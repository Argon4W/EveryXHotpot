package com.github.argon4w.fancytoys.codecs;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.RecordBuilder;

import java.util.function.Function;
import java.util.stream.Stream;

public class APMapEncoder<A, R> extends MapEncoder.Implementation<R> {

    private final A value;
    private final MapEncoder<A> aEncoder;
    private final MapEncoder<Function<A, R>> fEncoder;

    public APMapEncoder(A value, UniMapCodec.Encoder<A> aEncoder, UniMapCodec.Encoder<Function<A, R>> fEncoder) {
        this.value = value;
        this.aEncoder = aEncoder.mapEncoder();
        this.fEncoder = fEncoder.mapEncoder();
    }

    @Override
    public <T> RecordBuilder<T> encode(R input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        aEncoder.encode(value, ops, prefix);
        fEncoder.encode(a -> input, ops, prefix);

        return prefix;
    }

    @Override
    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return Stream.concat(aEncoder.keys(ops), fEncoder.keys(ops));
    }
}
