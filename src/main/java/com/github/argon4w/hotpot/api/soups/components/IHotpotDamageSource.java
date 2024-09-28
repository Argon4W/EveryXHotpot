package com.github.argon4w.hotpot.api.soups.components;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public interface IHotpotDamageSource {
    Wrapper EMPTY = new Wrapper(Either.right(new Empty()));
    Codec<Wrapper> CODEC = Codec.lazyInitialized(() -> Codec.either(Value.CODEC, Empty.CODEC).xmap(Wrapper::new, Wrapper::either));
    StreamCodec<RegistryFriendlyByteBuf, Wrapper> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> ByteBufCodecs.either(Value.STREAM_CODEC, Empty.STREAM_CODEC).map(Wrapper::new, Wrapper::either));

    void hurt(Entity entity);
    void hurt(Entity entity, Vec3 vec3);

    record Value(Holder<DamageType> damageTypeHolder, float amount) implements IHotpotDamageSource {
        public static final Codec<Value> CODEC = Codec.lazyInitialized(() ->
                RecordCodecBuilder.create(source -> source.group(
                        RegistryFixedCodec.create(Registries.DAMAGE_TYPE).fieldOf("damage_type").forGetter(Value::damageTypeHolder),
                        Codec.FLOAT.fieldOf("amount").forGetter(Value::amount)
                ).apply(source, Value::new))
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, Value> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
                StreamCodec.composite(
                        ByteBufCodecs.holderRegistry(Registries.DAMAGE_TYPE), Value::damageTypeHolder,
                        ByteBufCodecs.FLOAT, Value::amount,
                        Value::new
                )
        );

        @Override
        public void hurt(Entity entity) {
            entity.hurt(new DamageSource(damageTypeHolder), amount);
        }

        @Override
        public void hurt(Entity entity, Vec3 vec3) {
            entity.hurt(new DamageSource(damageTypeHolder, vec3), amount);
        }
    }

    class Empty implements IHotpotDamageSource {
        public static final Empty UNIT = new Empty();
        public static final Codec<Empty> CODEC = Codec.unit(UNIT);
        public static final StreamCodec<RegistryFriendlyByteBuf, Empty> STREAM_CODEC = StreamCodec.unit(UNIT);

        @Override
        public void hurt(Entity entity) {

        }

        @Override
        public void hurt(Entity entity, Vec3 vec3) {

        }
    }

    record Wrapper(Either<Value, Empty> either) implements IHotpotDamageSource {
        @Override
        public void hurt(Entity entity) {
            Either.unwrap(either).hurt(entity);
        }

        @Override
        public void hurt(Entity entity, Vec3 vec3) {
            Either.unwrap(either).hurt(entity, vec3);
        }
    }
}
