package com.github.argon4w.hotpot.soups.components.containers;

import com.github.argon4w.hotpot.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import com.github.argon4w.hotpot.soups.components.HotpotSoupComponentTypeSerializers;
import com.github.argon4w.hotpot.soups.components.IHotpotSoupComponentType;
import com.github.argon4w.hotpot.soups.components.IHotpotSoupComponentTypeSerializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class HotpotWaterLevelContainerSoupComponent extends AbstractHotpotSoupComponent {
    private double waterLevel;
    private double overflowWaterLevel;

    public HotpotWaterLevelContainerSoupComponent() {
        waterLevel = 1.0;
        overflowWaterLevel = 0.0;
    }

    public HotpotWaterLevelContainerSoupComponent(double waterLevel, double overflowWaterLevel) {
        this.waterLevel = waterLevel;
        this.overflowWaterLevel = overflowWaterLevel;
    }

    @Override
    public void setWaterLevel(double waterLevel, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
        this.waterLevel = hotpotBlockEntity.isInfiniteWater() ? 1.0 : Math.clamp(waterLevel, 0.0, 1.0);
        this.overflowWaterLevel = Math.max(0.0, waterLevel - 1.0);
    }

    @Override
    public IHotpotResult<Double> getWaterLevel(IHotpotResult<Double> result) {
        return result.isEmpty() ? IHotpotResult.success(waterLevel) : result;
    }

    @Override
    public IHotpotResult<Double> getOverflowWaterLevel(IHotpotResult<Double> result) {
        return result.isEmpty() ? IHotpotResult.success(overflowWaterLevel) : result;
    }

    @Override
    public void onDiscardOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
        overflowWaterLevel = 0.0;
    }

    @Override
    public boolean shouldSendToClient() {
        return true;
    }

    public double getWaterLevel() {
        return waterLevel;
    }

    public double getOverflowWaterLevel() {
        return overflowWaterLevel;
    }

    public static class Type implements IHotpotSoupComponentType<HotpotWaterLevelContainerSoupComponent> {
        public static final MapCodec<HotpotWaterLevelContainerSoupComponent> CODEC = RecordCodecBuilder.mapCodec(component -> component.group(
                Codec.DOUBLE.fieldOf("water_level").forGetter(HotpotWaterLevelContainerSoupComponent::getWaterLevel),
                Codec.DOUBLE.fieldOf("overflow_water_level").forGetter(HotpotWaterLevelContainerSoupComponent::getOverflowWaterLevel)
        ).apply(component, HotpotWaterLevelContainerSoupComponent::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, HotpotWaterLevelContainerSoupComponent> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, HotpotWaterLevelContainerSoupComponent::getWaterLevel,
                ByteBufCodecs.DOUBLE, HotpotWaterLevelContainerSoupComponent::getOverflowWaterLevel,
                HotpotWaterLevelContainerSoupComponent::new
        );

        @Override
        public MapCodec<HotpotWaterLevelContainerSoupComponent> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotWaterLevelContainerSoupComponent> getStreamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public HotpotWaterLevelContainerSoupComponent get() {
            return new HotpotWaterLevelContainerSoupComponent();
        }

        @Override
        public Holder<IHotpotSoupComponentTypeSerializer<?>> getSerializerHolder() {
            return HotpotSoupComponentTypeSerializers.WATER_LEVEL_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER;
        }
    }
}
