package com.github.argon4w.hotpot.codecs;

import com.google.common.base.Suppliers;
import com.mojang.serialization.*;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class LazyMapCodec<T> extends MapCodec<T> {
    private final String name;
    private final Supplier<MapCodec<T>> delegate;

    private LazyMapCodec(final String name, final Supplier<MapCodec<T>> delegate) {
        this.name = name;
        this.delegate = Suppliers.memoize(delegate::get);
    }

    @Override
    public <T1> Stream<T1> keys(DynamicOps<T1> ops) {
        return delegate.get().keys(ops);
    }

    @Override
    public <T1> DataResult<T> decode(DynamicOps<T1> ops, MapLike<T1> input) {
        return delegate.get().decode(ops, input);
    }

    @Override
    public <T1> RecordBuilder<T1> encode(T input, DynamicOps<T1> ops, RecordBuilder<T1> prefix) {
        return delegate.get().encode(input, ops, prefix);
    }

    @Override
    public String toString() {
        return "LazyMapCodec[" + name + ']';
    }

    public static <T> LazyMapCodec<T> of(Supplier<MapCodec<T>> delegate) {
        return new LazyMapCodec<>(delegate.toString(), delegate);
    }
}
