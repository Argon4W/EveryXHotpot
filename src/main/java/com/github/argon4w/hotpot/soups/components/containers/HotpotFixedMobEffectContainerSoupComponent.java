package com.github.argon4w.hotpot.soups.components.containers;

import com.github.argon4w.hotpot.HotpotMobEffectMap;
import com.github.argon4w.hotpot.codecs.LazyMapCodec;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import com.github.argon4w.hotpot.soups.components.HotpotSoupComponentTypeSerializers;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponentType;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponentTypeSerializer;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public class HotpotFixedMobEffectContainerSoupComponent extends AbstractHotpotSoupComponent implements IHotpotMobEffectContainerSoupComponent {
    private final HotpotMobEffectMap mobEffectMap;

    public HotpotFixedMobEffectContainerSoupComponent(HotpotMobEffectMap mobEffectMap) {
        this.mobEffectMap = mobEffectMap;
    }

    @Override
    public HotpotMobEffectMap getMobEffectMap() {
        return mobEffectMap.copy();
    }

    public static class Type implements IHotpotSoupComponentType<HotpotFixedMobEffectContainerSoupComponent> {
        private final HotpotMobEffectMap mobEffectMap;
        private final HotpotFixedMobEffectContainerSoupComponent unit;

        private final MapCodec<HotpotFixedMobEffectContainerSoupComponent> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, HotpotFixedMobEffectContainerSoupComponent> streamCodec;

        public Type(HotpotMobEffectMap mobEffectMap) {
            this.mobEffectMap = mobEffectMap;
            this.unit = new HotpotFixedMobEffectContainerSoupComponent(mobEffectMap);

            this.codec = MapCodec.unit(unit);
            this.streamCodec = StreamCodec.unit(unit);
        }

        @Override
        public MapCodec<HotpotFixedMobEffectContainerSoupComponent> getCodec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotFixedMobEffectContainerSoupComponent> getStreamCodec() {
            return streamCodec;
        }

        @Override
        public HotpotFixedMobEffectContainerSoupComponent createSoupComponent() {
            return unit;
        }

        @Override
        public Holder<IHotpotSoupComponentTypeSerializer<?>> getSerializerHolder() {
            return HotpotSoupComponentTypeSerializers.FIXED_MOB_EFFECT_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER;
        }

        public HotpotMobEffectMap getMobEffectMap() {
            return mobEffectMap;
        }
    }

    public static class Serializer implements IHotpotSoupComponentTypeSerializer<HotpotFixedMobEffectContainerSoupComponent> {
        public static final MapCodec<Type> CODEC = LazyMapCodec.of(() -> HotpotMobEffectMap.CODEC.fieldOf("effects").xmap(Type::new, Type::getMobEffectMap));
        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> HotpotMobEffectMap.STREAM_CODEC.map(Type::new, Type::getMobEffectMap));

        @Override
        public MapCodec<? extends IHotpotSoupComponentType<HotpotFixedMobEffectContainerSoupComponent>> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupComponentType<HotpotFixedMobEffectContainerSoupComponent>> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
