package com.github.argon4w.hotpot.soups.recipes.effects;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Optional;

public interface IHotpotRandomMobEffectKey {
    Codec<Wrapper> CODEC = Codec.either(Range.CODEC, Value.CODEC).xmap(Wrapper::new, Wrapper::either);
    StreamCodec<ByteBuf, Wrapper> STREAM_CODEC = ByteBufCodecs.either(Range.STREAM_CODEC, Value.STREAM_CODEC).map(Wrapper::new, Wrapper::either);

    Optional<MobEffectInstance> getEffect(HotpotRandomMobEffectMap mobEffectMap);

    record Range(int from, int to) implements IHotpotRandomMobEffectKey {
        public static final Codec<Range> CODEC = RecordCodecBuilder.create(ranged -> ranged.group(
                Codec.INT.fieldOf("from").forGetter(Range::from),
                Codec.INT.fieldOf("to").forGetter(Range::to)
        ).apply(ranged, Range::new));

        public static final StreamCodec<ByteBuf, Range> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, Range::from,
                ByteBufCodecs.INT, Range::to,
                Range::new
        );

        @Override
        public Optional<MobEffectInstance> getEffect(HotpotRandomMobEffectMap mobEffectMap) {
            return mobEffectMap.getRandom(from, to);
        }
    }

    record Value(int value) implements IHotpotRandomMobEffectKey {
        public static final Codec<Value> CODEC = Codec.INT.fieldOf("value").xmap(Value::new, Value::value).codec();
        public static final StreamCodec<ByteBuf, Value> STREAM_CODEC = ByteBufCodecs.INT.map(Value::new, Value::value);

        @Override
        public Optional<MobEffectInstance> getEffect(HotpotRandomMobEffectMap mobEffectMap) {
            return mobEffectMap.getClosest(value);
        }
    }

    record Wrapper(Either<Range, Value> either) implements IHotpotRandomMobEffectKey {
        @Override
        public Optional<MobEffectInstance> getEffect(HotpotRandomMobEffectMap mobEffectMap) {
            return Either.unwrap(either).getEffect(mobEffectMap);
        }
    }
}
