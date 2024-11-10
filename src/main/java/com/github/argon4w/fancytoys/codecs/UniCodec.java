package com.github.argon4w.fancytoys.codecs;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public record UniCodec<T>(Codec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {

    public static final UniCodec<Boolean> BOOL = new UniCodec<>(Codec.BOOL, ByteBufCodecs.BOOL.cast());
    public static final UniCodec<Byte> BYTE = new UniCodec<>(Codec.BYTE, ByteBufCodecs.BYTE.cast());
    public static final UniCodec<Short> SHORT = new UniCodec<>(Codec.SHORT, ByteBufCodecs.SHORT.cast());
    public static final UniCodec<Integer> INT = new UniCodec<>(Codec.INT, ByteBufCodecs.INT.cast());
    public static final UniCodec<Long> LONG = new UniCodec<>(Codec.LONG, ByteBufCodecs.VAR_LONG.cast());
    public static final UniCodec<Float> FLOAT = new UniCodec<>(Codec.FLOAT, ByteBufCodecs.FLOAT.cast());
    public static final UniCodec<Double> DOUBLE = new UniCodec<>(Codec.DOUBLE, ByteBufCodecs.DOUBLE.cast());
    public static final UniCodec<String> STRING = new UniCodec<>(Codec.STRING, ByteBufCodecs.STRING_UTF8.cast());
    public static final UniCodec<ItemStack> ITEM_STACK = new UniCodec<>(ItemStack.CODEC, ItemStack.STREAM_CODEC);
    public static final UniCodec<ItemStack> OPTIONAL_ITEM_STACK = new UniCodec<>(ItemStack.OPTIONAL_CODEC, ItemStack.OPTIONAL_STREAM_CODEC);
    public static final UniCodec<ResourceLocation> RESOURCE_LOCATION = new UniCodec<>(ResourceLocation.CODEC, ResourceLocation.STREAM_CODEC.cast());

    public UniCodec<List<T>> listOf() {
        return new UniCodec<>(
                codec.listOf(),
                streamCodec.apply(ByteBufCodecs.list()));
    }

    public UniCodec<List<T>> listOf(int maxSize) {
        return new UniCodec<>(
                codec.listOf(0, maxSize),
                streamCodec.apply(ByteBufCodecs.list(maxSize)));
    }

    public <R> UniCodec<R> map(Function<T, R> to, Function<R, T> from) {
        return new UniCodec<>(
                codec.xmap(to, from),
                streamCodec.map(to, from));
    }

    public UniMapCodec<T> fieldOf(String field) {
        return new UniMapCodec<>(
                codec.fieldOf(field),
                streamCodec);
    }

    public UniMapCodec<T> aliasedFieldOf(String... fields) {
        return new UniMapCodec<>(
                NeoForgeExtraCodecs.aliasedFieldOf(codec, fields),
                streamCodec
        );
    }

    public UniMapCodec<Optional<T>> optionalFieldOf(String field) {
        return new UniMapCodec<>(
                codec.optionalFieldOf(field),
                ByteBufCodecs.optional(streamCodec));
    }

    public UniMapCodec<T> optionalFieldOf(String field, T defaultValue) {
        return new UniMapCodec<>(
                codec.optionalFieldOf(field, defaultValue),
                streamCodec);
    }

    public <R> UniCodec<R> dispatch(Function<R, T> type, Function<T, UniMapCodec<R>> mapCodec) {
        return new UniCodec<>(
                codec.dispatch(type, t -> mapCodec.apply(t).mapCodec()),
                streamCodec.dispatch(type, t -> mapCodec.apply(t).streamCodec()));
    }

    public <R> UniCodec<R> dispatch(String label, Function<R, T> type, Function<T, UniMapCodec<R>> mapCodec) {
        return new UniCodec<>(
                codec.dispatch(label, type, t -> mapCodec.apply(t).mapCodec()),
                streamCodec.dispatch(type, t -> mapCodec.apply(t).streamCodec()));
    }

    public <R> UniMapCodec<R> dispatchMap(Function<R, T> type, Function<T, UniMapCodec<R>> mapCodec) {
        return new UniMapCodec<>(
                codec.dispatchMap(type, t -> mapCodec.apply(t).mapCodec()),
                streamCodec.dispatch(type, t -> mapCodec.apply(t).streamCodec()));
    }

    public <R> UniMapCodec<R> dispatchMap(String label, Function<R, T> type, Function<T, UniMapCodec<R>> mapCodec) {
        return new UniMapCodec<>(
                codec.dispatchMap(label, type, t -> mapCodec.apply(t).mapCodec()),
                streamCodec.dispatch(type, t -> mapCodec.apply(t).streamCodec()));
    }

    public static <T> UniCodec<T> unit(T unit) {
        return new UniCodec<>(
                Codec.unit(unit),
                NeoForgeStreamCodecs.uncheckedUnit(unit));
    }

    public static <T> UniCodec<T> unit(Supplier<T> supplier) {
        return new UniCodec<>(
                Codec.unit(supplier),
                StreamCodec.of((buffer, value) -> {}, buffer -> supplier.get())
        );
    }

    public static <T1, T2> UniCodec<Pair<T1, T2>> pair(UniCodec<T1> codec1, UniCodec<T2> codec2) {
        return new UniCodec<>(
                Codec.pair(codec1.codec, codec2.codec),
                new PairStreamCodec<>(codec1.streamCodec, codec2.streamCodec)
        );
    }

    public static <T> UniCodec<T> lazy(Supplier<UniCodec<T>> supplier) {
        return new UniCodec<>(
                Codec.lazyInitialized(() -> supplier.get().codec),
                NeoForgeStreamCodecs.lazy(() -> supplier.get().streamCodec));
    }

    public static <K, V> UniCodec<Map<K, V>> map(UniCodec<K> keyCodec, UniCodec<V> valueCodec) {
        return new UniCodec<>(
                Codec.unboundedMap(keyCodec.codec, valueCodec.codec),
                ByteBufCodecs.map(LinkedHashMap::new, keyCodec.streamCodec, valueCodec.streamCodec));
    }

    public static <T1, T2> UniCodec<Either<T1, T2>> either(UniCodec<T1> codec1, UniCodec<T2> codec2) {
        return new UniCodec<>(
                Codec.either(codec1.codec, codec2.codec),
                ByteBufCodecs.either(codec1.streamCodec, codec2.streamCodec));
    }

    public static <T> UniCodec<ResourceKey<T>> resourceKey(ResourceKey<? extends Registry<T>> registryKey) {
        return new UniCodec<>(
                ResourceKey.codec(registryKey),
                ResourceKey.streamCodec(registryKey).cast());
    }

    public static <T> UniCodec<T> registry(Registry<T> registry) {
        return new UniCodec<>(
                registry.byNameCodec(),
                ByteBufCodecs.registry(registry.key())
        );
    }

    public static <T> UniCodec<Holder<T>> holderRegistry(Registry<T> registry) {
        return new UniCodec<>(
                registry.holderByNameCodec(),
                ByteBufCodecs.holderRegistry(registry.key())
        );
    }

    public static <T> UniCodec<Holder<T>> holderRegistry(ResourceKey<? extends Registry<T>> registryKey) {
        return new UniCodec<>(
                RegistryFixedCodec.create(registryKey),
                ByteBufCodecs.holderRegistry(registryKey)
        );
    }

    public static <T> UniCodec<Holder<T>> holder(ResourceKey<? extends Registry<T>> registryKey, UniCodec<T> elementCodec) {
        return new UniCodec<>(
                RegistryFileCodec.create(registryKey, elementCodec.codec),
                ByteBufCodecs.holder(registryKey, elementCodec.streamCodec)
        );
    }

    public static <T> UniCodec<Holder<T>> holder(ResourceKey<? extends Registry<T>> registryKey, UniCodec<T> elementCodec, boolean allowInline) {
        return new UniCodec<>(
                RegistryFileCodec.create(registryKey, elementCodec.codec, allowInline),
                ByteBufCodecs.holder(registryKey, elementCodec.streamCodec)
        );
    }

    public static <T> UniCodec<T> recursive(String name, Function<UniCodec<T>, UniCodec<T>> wrapped) {
        return new UniCodec<>(
                Codec.recursive(name, codec -> wrapped.apply(new UniCodec<>(codec, StreamCodec.recursive(streamCodec -> wrapped.apply(new UniCodec<>(codec, streamCodec)).streamCodec))).codec),
                StreamCodec.recursive(streamCodec -> wrapped.apply(new UniCodec<>(Codec.recursive(name, codec -> wrapped.apply(new UniCodec<>(codec, streamCodec)).codec), streamCodec)).streamCodec));
    }
}
