package com.github.argon4w.hotpot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.NonNullList;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

public record IndexHolder<T>(int index, T value) {
    public IndexHolder<T> mapIndex(IntUnaryOperator operator) {
        return new IndexHolder<>(operator.applyAsInt(index), value);
    }

    public <R> IndexHolder<R> mapValue(Function<T, R> function) {
        return new IndexHolder<>(index, function.apply(value));
    }

    public static <C> Comparator<C> getIndexComparator(Function<C, IndexHolder<?>> function) {
        return Comparator.comparingInt(c -> function.apply(c).index);
    }

    public static <T> Codec<IndexHolder<T>> getIndexedCodec(MapCodec<T> codec) {
        return getIndexedCodec(codec, "index");
    }

    public static <T> MapCodec<IndexHolder<T>> getIndexedMapCodec(MapCodec<T> codec) {
        return getIndexedMapCodec(codec, "index");
    }

    public static <T> Codec<IndexHolder<T>> getIndexedCodec(MapCodec<T> codec, String label) {
        return Codec.INT.dispatch(label, IndexHolder::index, i -> codec.xmap(t -> new IndexHolder<>(i, t), IndexHolder::value));
    }

    public static <T> MapCodec<IndexHolder<T>> getIndexedMapCodec(MapCodec<T> codec, String label) {
        return Codec.INT.dispatchMap(label, IndexHolder::index, i -> codec.xmap(t -> new IndexHolder<>(i, t), IndexHolder::value));
    }

    public static <B extends ByteBuf, T> StreamCodec<B, IndexHolder<T>> getIndexedStreamCodec(StreamCodec<B, T> streamCodec) {
        return ByteBufCodecs.INT.<B>cast().dispatch(IndexHolder::index, i -> streamCodec.map(t -> new IndexHolder<>(i, t), IndexHolder::value));
    }

    public static <T> Codec<NonNullList<T>> getSizedNonNullCodec(Codec<IndexHolder<T>> codec, int size, T defaultValue) {
        return codec.listOf().xmap(list -> list.stream().collect(() -> NonNullList.withSize(size, defaultValue), (nonNullList, holder) -> nonNullList.set(holder.index, holder.value), nop()), list -> IntStream.range(0, list.size()).mapToObj(i -> new IndexHolder<>(i, list.get(i))).toList());
    }

    public static <B extends ByteBuf, T> StreamCodec<B, List<T>> getSortedListStreamCodec(StreamCodec<B, IndexHolder<T>> streamCodec) {
        return streamCodec.apply(ByteBufCodecs.list()).map(list -> list.stream().sorted(getIndexComparator(Function.identity())).map(IndexHolder::value).toList(), list -> IntStream.range(0, list.size()).mapToObj(i -> new IndexHolder<>(i, list.get(i))).toList());
    }

    public static <T1, T2>BiConsumer<T1, T2> nop() {
        return (t1, t2) -> {};
    }
}
