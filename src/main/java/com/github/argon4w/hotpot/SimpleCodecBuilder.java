package com.github.argon4w.hotpot;

import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.function.BiFunction;
import java.util.function.Function;

public class SimpleCodecBuilder {
    public static <T, T1> Builder1<T, T1> fieldOf(Codec<T1> codec, String field, Function<T, T1> function) {
        return new Builder1<>(codec, field, function);
    }

    public record Builder1<T, T1>(Codec<T1> codec, String field, Function<T, T1> function) {
        public <T2> Builder2<T, T1, T2> fieldOf(Codec<T2> codec, String field, Function<T, T2> function) {
            return new Builder2<>(this, codec, field, function);
        }

        public Codec<T> build(Function<T1, T> constructor) {
            return RecordCodecBuilder.create((instance) -> instance.group(
                    codec.fieldOf(field).forGetter(function)
            ).apply(instance, constructor));
        }

        public Codec<T> buildLazy(Function<T1, T> constructor) {
            return Codec.lazyInitialized(() -> build(constructor));
        }
    }

    public record Builder2<T, T1, T2>(Builder1<T, T1> builder1, Codec<T2> codec, String field, Function<T, T2> function) {
        public <T3> Builder3<T, T1, T2, T3> fieldOf(Codec<T3> codec, String field, Function<T, T3> function) {
            return new Builder3<>(this, codec, field, function);
        }

        public Codec<T> build(BiFunction<T1, T2, T> constructor) {
            return RecordCodecBuilder.create((instance) -> instance.group(
                    builder1.codec.fieldOf(builder1.field).forGetter(builder1.function),
                    codec.fieldOf(field).forGetter(function)
            ).apply(instance, constructor));
        }

        public Codec<T> buildLazy(BiFunction<T1, T2, T> constructor) {
            return Codec.lazyInitialized(() -> build(constructor));
        }
    }

    public record Builder3<T, T1, T2, T3>(Builder2<T, T1, T2> builder2, Codec<T3> codec, String field, Function<T, T3> function) {
        public <T4> Builder4<T, T1, T2, T3, T4> fieldOf(Codec<T4> codec, String field, Function<T, T4> function) {
            return new Builder4<>(this, codec, field, function);
        }

        public Codec<T> build(Function3<T1, T2, T3, T> constructor) {
            Builder1<T, T1> builder1 = builder2.builder1;

            return RecordCodecBuilder.create((instance) -> instance.group(
                    builder1.codec.fieldOf(builder1.field).forGetter(builder1.function),
                    builder2.codec.fieldOf(builder2.field).forGetter(builder2.function),
                    codec.fieldOf(field).forGetter(function)
            ).apply(instance, constructor));
        }

        public Codec<T> buildLazy(Function3<T1, T2, T3, T> constructor) {
            return Codec.lazyInitialized(() -> build(constructor));
        }
    }

    public record Builder4<T, T1, T2, T3, T4>(Builder3<T, T1, T2, T3> builder3, Codec<T4> codec, String field, Function<T, T4> function) {
        public <T5> Builder5<T, T1, T2, T3, T4, T5> fieldOf(Codec<T5> codec, String field, Function<T, T5> function) {
            return new Builder5<>(this, codec, field, function);
        }

        public Codec<T> build(Function4<T1, T2, T3, T4, T> constructor) {
            Builder1<T, T1> builder1 = builder3.builder2.builder1;
            Builder2<T, T1, T2> builder2 = builder3.builder2;

            return RecordCodecBuilder.create((instance) -> instance.group(
                    builder1.codec.fieldOf(builder1.field).forGetter(builder1.function),
                    builder2.codec.fieldOf(builder2.field).forGetter(builder2.function),
                    builder3.codec.fieldOf(builder3.field).forGetter(builder3.function),
                    codec.fieldOf(field).forGetter(function)
            ).apply(instance, constructor));
        }

        public Codec<T> buildLazy(Function4<T1, T2, T3, T4, T> constructor) {
            return Codec.lazyInitialized(() -> build(constructor));
        }
    }

    public record Builder5<T, T1, T2, T3, T4, T5>(Builder4<T, T1, T2, T3, T4> builder4, Codec<T5> codec, String field, Function<T, T5> function) {
        public Codec<T> build(Function5<T1, T2, T3, T4, T5, T> constructor) {
            Builder1<T, T1> builder1 = builder4.builder3.builder2.builder1;
            Builder2<T, T1, T2> builder2 = builder4.builder3.builder2;
            Builder3<T, T1, T2, T3> builder3 = builder4.builder3;

            return RecordCodecBuilder.create((instance) -> instance.group(
                    builder1.codec.fieldOf(builder1.field).forGetter(builder1.function),
                    builder2.codec.fieldOf(builder2.field).forGetter(builder2.function),
                    builder3.codec.fieldOf(builder3.field).forGetter(builder3.function),
                    builder4.codec.fieldOf(builder4.field).forGetter(builder4.function),
                    codec.fieldOf(field).forGetter(function)
            ).apply(instance, constructor));
        }

        public Codec<T> buildLazy(Function5<T1, T2, T3, T4, T5, T> constructor) {
            return Codec.lazyInitialized(() -> build(constructor));
        }
    }

    public record Builder6<T, T1, T2, T3, T4, T5, T6>(Builder5<T, T1, T2, T3, T4, T5> builder5, Codec<T6> codec, String field, Function<T, T6> function) {
        public Codec<T> build(Function6<T1, T2, T3, T4, T5, T6, T> constructor) {
            Builder1<T, T1> builder1 = builder5.builder4.builder3.builder2.builder1;
            Builder2<T, T1, T2> builder2 = builder5.builder4.builder3.builder2;
            Builder3<T, T1, T2, T3> builder3 = builder5.builder4.builder3;
            Builder4<T, T1, T2, T3, T4> builder4 = builder5.builder4;

            return RecordCodecBuilder.create((instance) -> instance.group(
                    builder1.codec.fieldOf(builder1.field).forGetter(builder1.function),
                    builder2.codec.fieldOf(builder2.field).forGetter(builder2.function),
                    builder3.codec.fieldOf(builder3.field).forGetter(builder3.function),
                    builder4.codec.fieldOf(builder4.field).forGetter(builder4.function),
                    builder5.codec.fieldOf(builder5.field).forGetter(builder5.function),
                    codec.fieldOf(field).forGetter(function)
            ).apply(instance, constructor));
        }

        public Codec<T> buildLazy(Function6<T1, T2, T3, T4, T5, T6, T> constructor) {
            return Codec.lazyInitialized(() -> build(constructor));
        }
    }
}
