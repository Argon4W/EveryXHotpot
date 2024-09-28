package com.github.argon4w.hotpot.client.soups.effects;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.client.soups.effects.IHotpotSoupClientTickEffect;
import com.github.argon4w.hotpot.api.client.soups.effects.IHotpotSoupClientTickEffectSerializer;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.codecs.LazyMapCodec;
import com.github.argon4w.hotpot.soups.components.HotpotSoupComponentTypeSerializers;
import com.github.argon4w.hotpot.soups.components.containers.HotpotPunishCooldownContainerSoupComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;

public record HotpotVaporEffect(ParticleType<?> particleType, float ratePerTick, int minAmountPerTick, int maxAmountPerTick, float xOffset, float yOffset, float zOffset, float xScale, float zScale, float xSpeed, float ySpeed, float zSpeed) implements IHotpotSoupClientTickEffect {
    @Override
    public void tick(LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        if (hotpotBlockEntity.getSoup().getComponentsByType(HotpotSoupComponentTypeSerializers.PUNISH_COOLDOWN_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER).stream().mapToInt(HotpotPunishCooldownContainerSoupComponent::getEmptyWaterPunishCooldown).sum() > 0) {
            return;
        }

        RandomSource randomSource = pos.getRandomSource();

        if (randomSource.nextFloat() > ratePerTick) {
            return;
        }

        BlockPos blockPos = pos.pos();
        float x = blockPos.getX();
        float y = blockPos.getY();
        float z = blockPos.getZ();

        float xRandom = randomSource.nextFloat();
        float zRandom = randomSource.nextFloat();

        float positionX = x + xOffset + (xRandom * (xScale * 2) - xScale);
        float positionY = y + yOffset;
        float positionZ = z + zOffset + (zRandom * (zScale * 2) - zScale);

        int amountPerTick = randomSource.nextInt(minAmountPerTick, maxAmountPerTick) + 1;

        for (int i = 0; i < amountPerTick; i ++) {
            pos.addParticle(particleType, positionX, positionY, positionZ, xSpeed, ySpeed, zSpeed);
        }
    }

    @Override
    public Holder<IHotpotSoupClientTickEffectSerializer<?>> getSerializerHolder() {
        return HotpotSoupClientTickEffects.VAPOR_EFFECT_SERIALIZER;
    }

    public static class Serializer implements IHotpotSoupClientTickEffectSerializer<HotpotVaporEffect> {
        public static final MapCodec<HotpotVaporEffect> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(effect -> effect.group(
                        BuiltInRegistries.PARTICLE_TYPE.byNameCodec().fieldOf("particle_type").forGetter(HotpotVaporEffect::particleType),
                        Codec.FLOAT.fieldOf("rate_per_tick").forGetter(HotpotVaporEffect::ratePerTick),
                        Codec.INT.fieldOf("min_amount_per_tick").forGetter(HotpotVaporEffect::minAmountPerTick),
                        Codec.INT.fieldOf("max_amount_per_tick").forGetter(HotpotVaporEffect::maxAmountPerTick),
                        Codec.FLOAT.fieldOf("x_offset").forGetter(HotpotVaporEffect::xOffset),
                        Codec.FLOAT.fieldOf("y_offset").forGetter(HotpotVaporEffect::yOffset),
                        Codec.FLOAT.fieldOf("z_offset").forGetter(HotpotVaporEffect::zOffset),
                        Codec.FLOAT.fieldOf("x_scale").forGetter(HotpotVaporEffect::xScale),
                        Codec.FLOAT.fieldOf("z_scale").forGetter(HotpotVaporEffect::zScale),
                        Codec.FLOAT.fieldOf("x_speed").forGetter(HotpotVaporEffect::xSpeed),
                        Codec.FLOAT.fieldOf("y_speed").forGetter(HotpotVaporEffect::ySpeed),
                        Codec.FLOAT.fieldOf("z_speed").forGetter(HotpotVaporEffect::zSpeed)
                ).apply(effect, HotpotVaporEffect::new))
        );

        @Override
        public MapCodec<HotpotVaporEffect> getCodec() {
            return CODEC;
        }
    }
}
