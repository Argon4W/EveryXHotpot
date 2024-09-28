package com.github.argon4w.hotpot.soups.components.ticks;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import com.github.argon4w.hotpot.soups.components.HotpotSoupComponentTypeSerializers;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponentType;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponentTypeSerializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class HotpotDropActivenessSoupComponent extends AbstractHotpotSoupComponent {
    private final double activenessDropRate;

    public HotpotDropActivenessSoupComponent(double activenessDropRate) {
        this.activenessDropRate = activenessDropRate;
    }

    @Override
    public void onTick(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
        soup.getComponentsByType(HotpotSoupComponentTypeSerializers.ACTIVENESS_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER).forEach(component -> component.setActiveness(component.getActiveness() - activenessDropRate / 20.0 / 60.0));
    }

    public static class Type implements IHotpotSoupComponentType<HotpotDropActivenessSoupComponent> {
        private final double activenessDropRate;
        private final HotpotDropActivenessSoupComponent unit;

        private final MapCodec<HotpotDropActivenessSoupComponent> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, HotpotDropActivenessSoupComponent> streamCodec;

        public Type(double activenessDropRate) {
            this.activenessDropRate = activenessDropRate;
            this.unit = new HotpotDropActivenessSoupComponent(activenessDropRate);

            this.codec = MapCodec.unit(unit);
            this.streamCodec = StreamCodec.unit(unit);
        }

        @Override
        public MapCodec<HotpotDropActivenessSoupComponent> getCodec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotDropActivenessSoupComponent> getStreamCodec() {
            return streamCodec;
        }

        @Override
        public HotpotDropActivenessSoupComponent get() {
            return unit;
        }

        @Override
        public Holder<IHotpotSoupComponentTypeSerializer<?>> getSerializerHolder() {
            return HotpotSoupComponentTypeSerializers.DROP_ACTIVENESS_SOUP_COMPONENT_TYPE_SERIALIZER;
        }

        public double getActivenessDropRate() {
            return activenessDropRate;
        }
    }

    public static class Serializer implements IHotpotSoupComponentTypeSerializer<HotpotDropActivenessSoupComponent> {
        public static final MapCodec<Type> CODEC = Codec.DOUBLE.fieldOf("activeness_drop_rate").xmap(Type::new, Type::getActivenessDropRate);
        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = ByteBufCodecs.DOUBLE.<RegistryFriendlyByteBuf>cast().map(Type::new, Type::getActivenessDropRate);

        @Override
        public MapCodec<? extends IHotpotSoupComponentType<HotpotDropActivenessSoupComponent>> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupComponentType<HotpotDropActivenessSoupComponent>> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
