package com.github.argon4w.hotpot.soups.components.ticks;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class HotpotDropWaterLevelSoupComponent extends AbstractHotpotSoupComponent {
    private final double waterLevelDropRate;

    public HotpotDropWaterLevelSoupComponent(double waterLevelDropRate) {
        this.waterLevelDropRate = waterLevelDropRate;
    }

    @Override
    public void onTick(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
        soup.setWaterLevel(soup.getWaterLevel() - (hotpotBlockEntity.isInfiniteWater() ? 0 : waterLevelDropRate) / 20.0 / 60.0, hotpotBlockEntity, pos);
    }

    public static class Type implements IHotpotSoupComponentType<HotpotDropWaterLevelSoupComponent> {
        private final double waterLevelDropRate;
        private final HotpotDropWaterLevelSoupComponent unit;

        private final MapCodec<HotpotDropWaterLevelSoupComponent> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, HotpotDropWaterLevelSoupComponent> streamCodec;

        public Type(double waterLevelDropRate) {
            this.waterLevelDropRate = waterLevelDropRate;
            this.unit = new HotpotDropWaterLevelSoupComponent(waterLevelDropRate);

            this.codec = MapCodec.unit(unit);
            this.streamCodec = StreamCodec.unit(unit);
        }

        @Override
        public MapCodec<HotpotDropWaterLevelSoupComponent> getCodec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotDropWaterLevelSoupComponent> getStreamCodec() {
            return streamCodec;
        }

        @Override
        public HotpotDropWaterLevelSoupComponent get() {
            return unit;
        }

        @Override
        public Holder<IHotpotSoupComponentTypeSerializer<?>> getSerializerHolder() {
            return HotpotSoupComponentTypeSerializers.DROP_WATER_LEVEL_SOUP_COMPONENT_TYPE_SERIALIZER;
        }

        public double getWaterLevelDropRate() {
            return waterLevelDropRate;
        }
    }

    public static class Serializer implements IHotpotSoupComponentTypeSerializer<HotpotDropWaterLevelSoupComponent> {
        public static final MapCodec<Type> CODEC = Codec.DOUBLE.fieldOf("water_level_drop_rate").xmap(Type::new, Type::getWaterLevelDropRate);
        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = ByteBufCodecs.DOUBLE.<RegistryFriendlyByteBuf>cast().map(Type::new, Type::getWaterLevelDropRate);

        @Override
        public MapCodec<? extends IHotpotSoupComponentType<HotpotDropWaterLevelSoupComponent>> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupComponentType<HotpotDropWaterLevelSoupComponent>> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
