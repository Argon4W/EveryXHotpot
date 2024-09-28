package com.github.argon4w.hotpot.soups.components.updates;

import com.github.argon4w.hotpot.api.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.api.contents.IHotpotContent;
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

public class HotpotIncreaseActivenessOnContentUpdateSoupComponent extends AbstractHotpotSoupComponent {
    private final double factor;
    private final double base;

    public HotpotIncreaseActivenessOnContentUpdateSoupComponent(double factor, double base) {
        this.factor = factor;
        this.base = base;
    }

    @Override
    public IHotpotResult<IHotpotContent> onContentUpdate(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<IHotpotContent> result) {
        return result.ifPresent(content -> soup.getComponentsByType(HotpotSoupComponentTypeSerializers.ACTIVENESS_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER).forEach(component -> component.setActiveness(base + factor * component.getActiveness())));
    }

    public static class Type implements IHotpotSoupComponentType<HotpotIncreaseActivenessOnContentUpdateSoupComponent> {
        private final double factor;
        private final double base;
        private final HotpotIncreaseActivenessOnContentUpdateSoupComponent unit;

        private final MapCodec<HotpotIncreaseActivenessOnContentUpdateSoupComponent> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, HotpotIncreaseActivenessOnContentUpdateSoupComponent> streamCodec;

        public Type(double factor, double base) {
            this.factor = factor;
            this.base = base;
            this.unit = new HotpotIncreaseActivenessOnContentUpdateSoupComponent(factor, base);

            this.codec = MapCodec.unit(unit);
            this.streamCodec = StreamCodec.unit(unit);
        }

        @Override
        public MapCodec<HotpotIncreaseActivenessOnContentUpdateSoupComponent> getCodec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotIncreaseActivenessOnContentUpdateSoupComponent> getStreamCodec() {
            return streamCodec;
        }

        @Override
        public HotpotIncreaseActivenessOnContentUpdateSoupComponent get() {
            return unit;
        }

        @Override
        public Holder<IHotpotSoupComponentTypeSerializer<?>> getSerializerHolder() {
            return HotpotSoupComponentTypeSerializers.INCREASE_ACTIVENESS_ON_CONTENT_UPDATE_SOUP_COMPONENT_TYPE_SERIALIZER;
        }

        public double getFactor() {
            return factor;
        }

        public double getBase() {
            return base;
        }
    }

    public static class Serializer implements IHotpotSoupComponentTypeSerializer<HotpotIncreaseActivenessOnContentUpdateSoupComponent> {
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
        public MapCodec<? extends IHotpotSoupComponentType<HotpotIncreaseActivenessOnContentUpdateSoupComponent>> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupComponentType<HotpotIncreaseActivenessOnContentUpdateSoupComponent>> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
