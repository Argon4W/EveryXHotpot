package com.github.argon4w.fancytoys.codecs;

import com.mojang.serialization.*;

import java.util.stream.Stream;

public class BuilderMapCodec<O> extends MapCodec<O> {

    private final UniRecordCodec<O, O> builder;

    public BuilderMapCodec(UniRecordCodec<O, O> builder) {
        this.builder = builder;
    }

    @Override
    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return builder.getDecoder().mapDecoder().keys(ops);
    }

    @Override
    public <T> DataResult<O> decode(DynamicOps<T> ops, MapLike<T> input) {
        return builder.getDecoder().mapDecoder().decode(ops, input);
    }

    @Override
    public <T> RecordBuilder<T> encode(O input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        return builder.getEncoder().apply(input).mapEncoder().encode(input, ops, prefix);
    }
}
