package com.github.argon4w.hotpot.client.soups.effects;

import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;

public record HotpotEmptyWaterSmokeEffect(ParticleType<?> particleType, int amountPerTick, float xOffset, float yOffset, float zOffset, float xScale, float zScale, float xSpeed, float ySpeed, float zSpeed) implements IHotpotSoupClientTickEffect {
    @Override
    public void tick(LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        if (hotpotBlockEntity.getWaterLevel() > 0) {
            return;
        }

        BlockPos blockPos = pos.pos();
        float x = blockPos.getX();
        float y = blockPos.getY();
        float z = blockPos.getZ();

        RandomSource randomSource = pos.getRandomSource();

        float xRandom = randomSource.nextFloat();
        float zRandom = randomSource.nextFloat();

        float positionX = x + xOffset + (xRandom * (xScale * 2) - xScale);
        float positionY = y + yOffset;
        float positionZ = z + zOffset + (zRandom * (zScale * 2) - zScale);

        for (int i = 0; i < amountPerTick; i ++) {
            pos.addParticle(particleType, positionX, positionY, positionZ, xSpeed, ySpeed, zSpeed);
        }
    }

    @Override
    public Holder<IHotpotSoupClientTickEffectSerializer<?>> getSerializerHolder() {
        return HotpotSoupClientTickEffects.EMPTY_WATER_SMOKE_EFFECT_SERIALIZER;
    }

    public static class Serializer implements IHotpotSoupClientTickEffectSerializer<HotpotEmptyWaterSmokeEffect> {
        public static final MapCodec<HotpotEmptyWaterSmokeEffect> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(effect -> effect.group(
                        BuiltInRegistries.PARTICLE_TYPE.byNameCodec().fieldOf("particle_type").forGetter(HotpotEmptyWaterSmokeEffect::particleType),
                        Codec.INT.fieldOf("amount_per_tick").forGetter(HotpotEmptyWaterSmokeEffect::amountPerTick),
                        Codec.FLOAT.fieldOf("x_offset").forGetter(HotpotEmptyWaterSmokeEffect::xOffset),
                        Codec.FLOAT.fieldOf("y_offset").forGetter(HotpotEmptyWaterSmokeEffect::yOffset),
                        Codec.FLOAT.fieldOf("z_offset").forGetter(HotpotEmptyWaterSmokeEffect::zOffset),
                        Codec.FLOAT.fieldOf("x_scale").forGetter(HotpotEmptyWaterSmokeEffect::xScale),
                        Codec.FLOAT.fieldOf("z_scale").forGetter(HotpotEmptyWaterSmokeEffect::zScale),
                        Codec.FLOAT.fieldOf("x_speed").forGetter(HotpotEmptyWaterSmokeEffect::xSpeed),
                        Codec.FLOAT.fieldOf("y_speed").forGetter(HotpotEmptyWaterSmokeEffect::ySpeed),
                        Codec.FLOAT.fieldOf("z_speed").forGetter(HotpotEmptyWaterSmokeEffect::zSpeed)
                ).apply(effect, HotpotEmptyWaterSmokeEffect::new))
        );

        @Override
        public MapCodec<HotpotEmptyWaterSmokeEffect> getCodec() {
            return CODEC;
        }
    }
}
