package com.github.argon4w.hotpot.soups.components.appendents;

import com.github.argon4w.hotpot.api.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import com.github.argon4w.hotpot.soups.components.HotpotSoupComponentTypeSerializers;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponentType;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponentTypeSerializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class HotpotGetExtraContentTickSpeedFromWaterLevelSoupComponent extends AbstractHotpotSoupComponent {
    private final double factor;
    private final double base;

    public HotpotGetExtraContentTickSpeedFromWaterLevelSoupComponent(double factor, double base) {
        this.factor = factor;
        this.base = base;
    }

    @Override
    public IHotpotResult<Double> getContentTickSpeed(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<Double> result) {
        return result.map(speed -> speed + base + factor * soup.getWaterLevel());
    }

    public static class Type implements IHotpotSoupComponentType<HotpotGetExtraContentTickSpeedFromWaterLevelSoupComponent> {
        private final double factor;
        private final double base;
        private final HotpotGetExtraContentTickSpeedFromWaterLevelSoupComponent unit;

        private final MapCodec<HotpotGetExtraContentTickSpeedFromWaterLevelSoupComponent> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, HotpotGetExtraContentTickSpeedFromWaterLevelSoupComponent> streamCodec;

        public Type(double factor, double base) {
            this.factor = factor;
            this.base = base;
            this.unit = new HotpotGetExtraContentTickSpeedFromWaterLevelSoupComponent(factor, base);

            this.codec = MapCodec.unit(unit);
            this.streamCodec = StreamCodec.unit(unit);
        }

        @Override
        public MapCodec<HotpotGetExtraContentTickSpeedFromWaterLevelSoupComponent> getCodec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotGetExtraContentTickSpeedFromWaterLevelSoupComponent> getStreamCodec() {
            return streamCodec;
        }

        @Override
        public HotpotGetExtraContentTickSpeedFromWaterLevelSoupComponent get() {
            return unit;
        }

        @Override
        public Holder<IHotpotSoupComponentTypeSerializer<?>> getSerializerHolder() {
            return HotpotSoupComponentTypeSerializers.GET_EXTRA_CONTENT_TICK_SPEED_FROM_WATER_LEVEL_SOUP_COMPONENT_TYPE_SERIALIZER;
        }

        public double getFactor() {
            return factor;
        }

        public double getBase() {
            return base;
        }
    }

    public static class Serializer implements IHotpotSoupComponentTypeSerializer<HotpotGetExtraContentTickSpeedFromWaterLevelSoupComponent> {
        public static final MapCodec<Type> CODEC = RecordCodecBuilder.mapCodec(type -> type.group(
                Codec.DOUBLE.optionalFieldOf("factor", 1.0).forGetter(Type::getFactor),
                Codec.DOUBLE.optionalFieldOf("base", 0.0).forGetter(Type::getBase)
        ).apply(type, Type::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, Type::getFactor,
                ByteBufCodecs.DOUBLE, Type::getBase,
                Type::new
        );

        @Override
        public MapCodec<? extends IHotpotSoupComponentType<HotpotGetExtraContentTickSpeedFromWaterLevelSoupComponent>> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupComponentType<HotpotGetExtraContentTickSpeedFromWaterLevelSoupComponent>> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
