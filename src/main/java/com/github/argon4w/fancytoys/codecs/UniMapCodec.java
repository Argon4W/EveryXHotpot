package com.github.argon4w.fancytoys.codecs;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.function.Function;
import java.util.function.Supplier;

public record UniMapCodec<T>(MapCodec<T> mapCodec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {

    public Encoder<T> encoder() {
        return new Encoder<>(
                mapCodec,
                streamCodec);
    }

    public Decoder<T> decoder() {
        return new Decoder<>(
                mapCodec,
                streamCodec);
    }

    public UniCodec<T> codec() {
        return new UniCodec<>(
                mapCodec.codec(),
                streamCodec);
    }

    public <R> UniMapCodec<R> map(Function<T, R> to, Function<R, T> from) {
        return new UniMapCodec<>(
                mapCodec.xmap(to, from),
                streamCodec.map(to, from)
        );
    }

    public <O> UniRecordCodec<O, T> forGetter(Function<O, T> getter) {
        return new UniRecordCodec<>(
                getter,
                o -> encoder(),
                decoder());
    }

    public static <T> UniMapCodec<T> unit(T unit) {
        return new UniMapCodec<>(
                MapCodec.unit(unit),
                NeoForgeStreamCodecs.uncheckedUnit(unit));
    }

    public static <T> UniMapCodec<T> lazy(Supplier<UniMapCodec<T>> supplier) {
        return new UniMapCodec<>(
                LazyMapCodec.of(() -> supplier.get().mapCodec),
                NeoForgeStreamCodecs.lazy(() -> supplier.get().streamCodec));
    }

    public static <T1, T2> UniMapCodec<Pair<T1, T2>> pair(UniMapCodec<T1> codec1, UniMapCodec<T2> codec2) {
        return new UniMapCodec<>(
                Codec.mapPair(codec1.mapCodec, codec2.mapCodec),
                new PairStreamCodec<>(codec1.streamCodec, codec2.streamCodec)
        );
    }

    public static <T1, T2> UniMapCodec<Either<T1, T2>> either(UniMapCodec<T1> codec1, UniMapCodec<T2> codec2) {
        return new UniMapCodec<>(
                Codec.mapEither(codec1.mapCodec, codec2.mapCodec),
                ByteBufCodecs.either(codec1.streamCodec, codec2.streamCodec)
        );
    }

    public static <T> UniMapCodec<T> recursive(String name, Function<UniCodec<T>, UniMapCodec<T>> wrapped) {
        return new UniMapCodec<>(
                MapCodec.recursive(name, codec -> wrapped.apply(new UniCodec<>(codec, StreamCodec.recursive(streamCodec -> wrapped.apply(new UniCodec<>(codec, streamCodec)).streamCodec))).mapCodec),
                StreamCodec.recursive(streamCodec -> wrapped.apply(new UniCodec<>(Codec.recursive(name, codec -> new UniCodec<>(codec, streamCodec).codec()), streamCodec)).streamCodec));
    }

    public record Encoder<T>(MapEncoder<T> mapEncoder, StreamEncoder<RegistryFriendlyByteBuf, T> streamEncoder) {

        public <R> Encoder<R> map(Function<R, T> mapper) {
            return new Encoder<>(mapEncoder.comap(mapper), (buffer, r) -> streamEncoder.encode(buffer, mapper.apply(r)));
        }

        public UniMapCodec<T> codec(UniMapCodec<T> decoder) {
            return new UniMapCodec<>(MapCodec.of(mapEncoder, decoder.mapCodec), StreamCodec.of(streamEncoder, decoder.streamCodec));
        }

        public static <T> Encoder<T> unit(T unit) {
            return new Encoder<>(MapCodec.unit(unit), NeoForgeStreamCodecs.uncheckedUnit(unit));
        }
    }

    public record Decoder<T>(MapDecoder<T> mapDecoder, StreamDecoder<RegistryFriendlyByteBuf, T> streamDecoder) {

        public <R> Decoder<R> map(Function<T, R> mapper) {
            return new Decoder<>(mapDecoder.map(mapper), buffer -> mapper.apply(streamDecoder.decode(buffer)));
        }

        public UniMapCodec<T> codec(Encoder<T> encoder) {
            return new UniMapCodec<>(MapCodec.of(encoder.mapEncoder, mapDecoder), StreamCodec.of(encoder.streamEncoder, streamDecoder));
        }

        public static <T> Decoder<T> unit(T unit) {
            return new Decoder<>(MapCodec.unit(unit), NeoForgeStreamCodecs.uncheckedUnit(unit));
        }
    }
}
