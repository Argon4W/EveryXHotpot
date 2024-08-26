package com.github.argon4w.hotpot.soups.components.containers;

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

public class HotpotActivenessContainerSoupComponent extends AbstractHotpotSoupComponent {
    private double activeness;
    private final double minActiveness;
    private final double maxActiveness;

    public HotpotActivenessContainerSoupComponent(double minActiveness, double maxActiveness) {
        this.minActiveness = minActiveness;
        this.maxActiveness = maxActiveness;
        this.activeness = 0.0f;
    }

    public HotpotActivenessContainerSoupComponent(double minActiveness, double maxActiveness, double activeness) {
        this.minActiveness = minActiveness;
        this.maxActiveness = maxActiveness;
        this.activeness = activeness;
    }

    public void setActiveness(double activeness) {
        this.activeness = Math.clamp(activeness, minActiveness, maxActiveness);
    }

    public double getActiveness() {
        return activeness;
    }

    public static class Type implements IHotpotSoupComponentType<HotpotActivenessContainerSoupComponent> {
        private final double minActiveness;
        private final double maxActiveness;

        private final MapCodec<HotpotActivenessContainerSoupComponent> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, HotpotActivenessContainerSoupComponent> streamCodec;

        public Type(double minActiveness, double maxActiveness) {
            this.minActiveness = minActiveness;
            this.maxActiveness = maxActiveness;

            this.codec = Codec.DOUBLE.fieldOf("activeness").xmap(activeness -> new HotpotActivenessContainerSoupComponent(minActiveness, maxActiveness, activeness), HotpotActivenessContainerSoupComponent::getActiveness);
            this.streamCodec = ByteBufCodecs.DOUBLE.<RegistryFriendlyByteBuf>cast().map(activeness -> new HotpotActivenessContainerSoupComponent(minActiveness, maxActiveness, activeness), HotpotActivenessContainerSoupComponent::getActiveness);
        }

        @Override
        public MapCodec<HotpotActivenessContainerSoupComponent> getCodec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotActivenessContainerSoupComponent> getStreamCodec() {
            return streamCodec;
        }

        @Override
        public HotpotActivenessContainerSoupComponent get() {
            return new HotpotActivenessContainerSoupComponent(minActiveness, maxActiveness);
        }

        @Override
        public Holder<IHotpotSoupComponentTypeSerializer<?>> getSerializerHolder() {
            return HotpotSoupComponentTypeSerializers.ACTIVENESS_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER;
        }

        public double getMinActiveness() {
            return minActiveness;
        }

        public double getMaxActiveness() {
            return maxActiveness;
        }
    }

    public static class Serializer implements IHotpotSoupComponentTypeSerializer<HotpotActivenessContainerSoupComponent> {
        public static final MapCodec<Type> CODEC = RecordCodecBuilder.mapCodec(type -> type.group(
                Codec.DOUBLE.optionalFieldOf("min_activeness", 0.0d).forGetter(Type::getMinActiveness),
                Codec.DOUBLE.optionalFieldOf("max_activeness", 1.0d).forGetter(Type::getMinActiveness)
        ).apply(type, Type::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, Type::getMinActiveness,
                ByteBufCodecs.DOUBLE, Type::getMaxActiveness,
                Type::new
        );

        @Override
        public MapCodec<? extends IHotpotSoupComponentType<HotpotActivenessContainerSoupComponent>> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupComponentType<HotpotActivenessContainerSoupComponent>> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
