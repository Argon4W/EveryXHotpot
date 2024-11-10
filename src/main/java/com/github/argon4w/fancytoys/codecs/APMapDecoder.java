package com.github.argon4w.fancytoys.codecs;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapLike;

import java.util.function.Function;
import java.util.stream.Stream;

public class APMapDecoder<A, R> extends MapDecoder.Implementation<R> {

    private final MapDecoder<A> aDecoder;
    private final MapDecoder<Function<A, R>> fDecoder;

    public APMapDecoder(UniMapCodec.Decoder<A> aDecoder, UniMapCodec.Decoder<Function<A, R>> fDecoder) {
        this.aDecoder = aDecoder.mapDecoder();
        this.fDecoder = fDecoder.mapDecoder();
    }

    @Override
    public <T> DataResult<R> decode(DynamicOps<T> ops, MapLike<T> input) {
        return aDecoder.decode(ops, input).flatMap(a -> fDecoder.decode(ops, input).map(f -> f.apply(a)));
    }

    @Override
    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return Stream.concat(aDecoder.keys(ops), fDecoder.keys(ops));
    }
}
