package com.github.argon4w.fancytoys.codecs;

import com.github.argon4w.fancytoys.functions.Unsupported;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.extensions.IHolderExtension;

public class Sorted<T> {

    protected final int pos;
    protected final T value;

    public Sorted(int pos, T value) {
        this.pos = pos;
        this.value = value;
    }

    public int pos() {
        return pos;
    }

    public T value() {
        return value;
    }

    public Sorted<T> mapPos(IntUnaryOperator operator) {
        return new Sorted<>(operator.applyAsInt(pos), value);
    }

    public Sorted<T> mapPos(IntSupplier supplier) {
        return new Sorted<>(supplier.getAsInt() , value);
    }

    public <R> Sorted<R> mapValue(R r) {
        return new Sorted<>(pos, r);
    }

    public <R> Sorted<R> mapValue(Function<T, R> function) {
        return new Sorted<>(pos, function.apply(value));
    }

    public static <T> Function<Sorted<T>, Sorted<T>> posMapper(IntUnaryOperator operator) {
        return sorted -> sorted.mapPos(operator);
    }

    public static <T> Function<Sorted<T>, Sorted<T>> posMapper(IntSupplier supplier) {
        return sorted -> sorted.mapPos(supplier);
    }

    public static <T, R> Function<Sorted<T>, Sorted<R>> valueMapper(Function<T, R> function) {
        return sorted -> sorted.mapValue(function);
    }

    public static <T> Comparator<Sorted<T>> comparator() {
        return Comparator.comparingInt(Sorted::pos);
    }

    public static <T> void set(List<T> list, Sorted<T> sorted) {
        list.set(sorted.pos(), sorted.value());
    }

    public static <T> UniCodec<Sorted<T>> uniCodec(UniMapCodec<T> codec) {
        return uniCodec(codec, "index");
    }

    public static <T> UniCodec<Sorted<T>> uniCodec(UniMapCodec<T> codec, String label) {
        return UniCodec.INT.dispatch(label, Sorted::pos, i -> codec.map(t -> new Sorted<>(i, t), Sorted::value));
    }

    public static <T> UniCodec<NonNullList<T>> uniCodec(UniCodec<Sorted<T>> codec, int size, T defaultValue) {
        return codec.listOf().map(list -> sort(list, size, defaultValue), Sorted::wrap);
    }

    public static <T> Codec<Sorted<T>> codec(MapCodec<T> codec) {
        return codec(codec, "index");
    }

    public static <T> Codec<Sorted<T>> codec(MapCodec<T> codec, String label) {
        return Codec.INT.dispatch(label, Sorted::pos, i -> codec.xmap(t -> new Sorted<>(i, t), Sorted::value));
    }

    public static <T> Codec<NonNullList<T>> codec(Codec<Sorted<T>> codec, int size, T defaultValue) {
        return codec.listOf().xmap(list -> sort(list, size, defaultValue), Sorted::wrap);
    }

    public static <B extends ByteBuf, T> StreamCodec<B, Sorted<T>> streamCodec(StreamCodec<B, T> streamCodec) {
        return ByteBufCodecs.INT.<B>cast().dispatch(Sorted::pos, i -> streamCodec.map(t -> new Sorted<>(i, t), Sorted::value));
    }

    public static <B extends ByteBuf, T> StreamCodec<B, NonNullList<T>> streamCodec(StreamCodec<B, Sorted<T>> streamCodec, int size, T defaultValue) {
        return streamCodec.apply(ByteBufCodecs.list()).map(list -> sort(list, size, defaultValue), Sorted::wrap);
    }

    public static <T> Optional<HolderLookup<T>> lookup(Sorted<Holder<T>> sorted) {
        return Optional.ofNullable(sorted.value).map(IHolderExtension::unwrapLookup);
    }

    public static <T> List<Sorted<T>> wrap(List<T> list) {
        return IntStream.range(0, list.size()).mapToObj(i -> new Sorted<>(i, list.get(i))).toList();
    }

    public static <T> NonNullList<T> sort(List<Sorted<T>> list, int size, T defaultValue) {
        return list.stream().collect(() -> NonNullList.withSize(size, defaultValue), Sorted::set, Unsupported.combinerConsumer());
    }
}
