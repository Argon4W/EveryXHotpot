package com.github.argon4w.fancytoys.codecs;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.function.Function;

public class UniRecordCodec<O, F> implements App<UniRecordCodec.Mu<O>, F> {

    private final Function<O, UniMapCodec.Encoder<F>> encoder;
    private final UniMapCodec.Decoder<F> decoder;
    private final Function<O, F> getter;

    public UniRecordCodec(Function<O, F> getter, Function<O, UniMapCodec.Encoder<F>> encoder, UniMapCodec.Decoder<F> decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.getter = getter;
    }

    public Function<O, UniMapCodec.Encoder<F>> getEncoder() {
        return encoder;
    }

    public UniMapCodec.Decoder<F> getDecoder() {
        return decoder;
    }

    public Function<O, F> getGetter() {
        return getter;
    }

    public static <O, F> UniRecordCodec<O, F> cast(App<Mu<O>, F> box) {
        return (UniRecordCodec<O, F>) box;
    }

    public static <O> UniCodec<O> codec(Function<Builder<O>, App<Mu<O>, O>> function) {
        return mapCodec(function).codec();
    }

    public static <O> UniMapCodec<O> mapCodec(Function<Builder<O>, App<Mu<O>, O>> function) {
        return mapCodec(cast(function.apply(new Builder<>())));
    }

    public static <O> UniMapCodec<O> mapCodec(UniRecordCodec<O, O> builder) {
        return new UniMapCodec<>(new BuilderMapCodec<>(builder), new BuilderStreamCodec<>(builder));
    }

    public static <O> UniCodec<O> lazyCodec(Function<Builder<O>, App<Mu<O>, O>> function) {
        return lazyMapCodec(function).codec();
    }

    public static <O> UniMapCodec<O> lazyMapCodec(Function<Builder<O>, App<Mu<O>, O>> function) {
        return lazyMapCodec(cast(function.apply(new Builder<>())));
    }

    public static <O> UniMapCodec<O> lazyMapCodec(UniRecordCodec<O, O> builder) {
        return new UniMapCodec<>(LazyMapCodec.of(() -> new BuilderMapCodec<>(builder)), NeoForgeStreamCodecs.lazy(() -> new BuilderStreamCodec<>(builder)));
    }

    public static final class Mu<O> implements K1 {

    }

    public static class Builder<O> implements Applicative<Mu<O>, Builder.Mu<O>> {

        @Override
        public <A, R> Function<App<UniRecordCodec.Mu<O>, A>, App<UniRecordCodec.Mu<O>, R>> lift1(App<UniRecordCodec.Mu<O>, Function<A, R>> function) {
            return app -> lift(cast(app), cast(function));
        }

        @Override
        public <A> App<UniRecordCodec.Mu<O>, A> point(A a) {
            return new UniRecordCodec<>(o -> a, o -> UniMapCodec.Encoder.unit(a), UniMapCodec.Decoder.unit(a));
        }

        @Override
        public <T, R> App<UniRecordCodec.Mu<O>, R> map(Function<? super T, ? extends R> func, App<UniRecordCodec.Mu<O>, T> ts) {
            return map(func, cast(ts));
        }

        public  <T, R> UniRecordCodec<O, R> map(Function<? super T, ? extends R> func, UniRecordCodec<O, T> tBuilder) {
            return new UniRecordCodec<>(tBuilder.getGetter().andThen(func), getMapEncoder(tBuilder), tBuilder.getDecoder().map(func::apply));
        }

        public static <O, T, R> Function<O, UniMapCodec.Encoder<R>> getMapEncoder(UniRecordCodec<O, T> tBuilder) {
            return o -> tBuilder.encoder.apply(o).map(r -> tBuilder.getGetter().apply(o));
        }

        public static  <O, A, R> UniRecordCodec<O, R> lift(UniRecordCodec<O, A> aBuilder, UniRecordCodec<O, Function<A, R>> fBuilder) {
            return new UniRecordCodec<>(o -> fBuilder.getGetter().apply(o).apply(aBuilder.getGetter().apply(o)), getAPEncoder(aBuilder, fBuilder), getAPDecoder(aBuilder, fBuilder));
        }

        public static <O, A, R> Function<O, UniMapCodec.Encoder<R>> getAPEncoder(UniRecordCodec<O, A> aBuilder, UniRecordCodec<O, Function<A, R>> fBuilder) {
            return o -> getAPEncoder(aBuilder.getGetter().apply(o), aBuilder.getEncoder().apply(o), fBuilder.getEncoder().apply(o));
        }

        public static <A, R> UniMapCodec.Encoder<R> getAPEncoder(A value, UniMapCodec.Encoder<A> aEncoder, UniMapCodec.Encoder<Function<A, R>> fEncoder) {
            return new UniMapCodec.Encoder<>(new APMapEncoder<>(value, aEncoder, fEncoder), new APStreamEncoder<>(value, aEncoder, fEncoder));
        }

        public static <O, A, R> UniMapCodec.Decoder<R> getAPDecoder(UniRecordCodec<O, A> aBuilder, UniRecordCodec<O, Function<A, R>> fBuilder) {
            return getAPDecoder(aBuilder.getDecoder(), fBuilder.getDecoder());
        }

        public static <A, R> UniMapCodec.Decoder<R> getAPDecoder(UniMapCodec.Decoder<A> aDecoder, UniMapCodec.Decoder<Function<A, R>> fDecoder) {
            return new UniMapCodec.Decoder<>(new APMapDecoder<>(aDecoder, fDecoder), new APStreamDecoder<>(aDecoder, fDecoder));
        }

        private static final class Mu<O> implements Applicative.Mu {

        }
    }
}
