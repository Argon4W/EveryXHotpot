package com.github.argon4w.hotpot.soups.components.containers;

import com.github.argon4w.hotpot.HotpotMobEffectMap;
import com.github.argon4w.hotpot.codecs.LazyMapCodec;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public class HotpotDynamicMobEffectContainerSoupComponent extends AbstractHotpotSoupComponent implements IHotpotMobEffectContainerSoupComponent {
    private final int size;
    private boolean updated;
    private HotpotMobEffectMap mobEffectMap;

    public HotpotDynamicMobEffectContainerSoupComponent(int size, boolean updated, HotpotMobEffectMap mobEffectMap) {
        this.size = size;
        this.updated = updated;
        this.mobEffectMap = mobEffectMap;
    }

    public HotpotDynamicMobEffectContainerSoupComponent(int size) {
        this.size = size;
        this.updated = false;
        this.mobEffectMap = new HotpotMobEffectMap();
    }

    public void trimEffectMap() {
        while (mobEffectMap.size() > size) {
            mobEffectMap.pollFirstEntry();
        }
    }

    public void putEffect(MobEffectInstance mobEffectInstance) {
        mobEffectMap.putEffect(mobEffectInstance);
        this.updated = true;
        trimEffectMap();
    }

    public void setMobEffectMap(HotpotMobEffectMap mobEffectMap) {
        this.mobEffectMap = mobEffectMap.copy();
        trimEffectMap();
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated() {
        this.updated = false;
    }

    @Override
    public HotpotMobEffectMap getMobEffectMap() {
        return mobEffectMap.copy();
    }

    public static class Type implements IHotpotSoupComponentType<HotpotDynamicMobEffectContainerSoupComponent> {
        private final int size;

        private final Codec<HotpotMobEffectMap> sizedMobEffectMapCodec;
        private final StreamCodec<RegistryFriendlyByteBuf, HotpotMobEffectMap> sizedMobEffectMapStreamCodec;

        private final MapCodec<HotpotDynamicMobEffectContainerSoupComponent> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, HotpotDynamicMobEffectContainerSoupComponent> streamCodec;

        public Type(int size) {
            this.size = size;

            this.sizedMobEffectMapCodec = HotpotMobEffectMap.getSizedCodec(size);
            this.sizedMobEffectMapStreamCodec = HotpotMobEffectMap.getSizedStreamCodec(size);

            this.codec = LazyMapCodec.of(() ->
                    RecordCodecBuilder.mapCodec(component -> component.group(
                            Codec.BOOL.fieldOf("updated").forGetter(HotpotDynamicMobEffectContainerSoupComponent::isUpdated),
                            sizedMobEffectMapCodec.fieldOf("effects").forGetter(HotpotDynamicMobEffectContainerSoupComponent::getMobEffectMap)
                    ).apply(component, (updated, effects) -> new HotpotDynamicMobEffectContainerSoupComponent(size, updated, effects)))
            );

            this.streamCodec = NeoForgeStreamCodecs.lazy(() ->
                    StreamCodec.composite(
                            ByteBufCodecs.BOOL, HotpotDynamicMobEffectContainerSoupComponent::isUpdated,
                            sizedMobEffectMapStreamCodec, HotpotDynamicMobEffectContainerSoupComponent::getMobEffectMap,
                            (updated, effects) -> new HotpotDynamicMobEffectContainerSoupComponent(size, updated, effects)
                    )
            );
        }

        @Override
        public MapCodec<HotpotDynamicMobEffectContainerSoupComponent> getCodec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotDynamicMobEffectContainerSoupComponent> getStreamCodec() {
            return streamCodec;
        }

        @Override
        public HotpotDynamicMobEffectContainerSoupComponent get() {
            return new HotpotDynamicMobEffectContainerSoupComponent(size);
        }

        @Override
        public Holder<IHotpotSoupComponentTypeSerializer<?>> getSerializerHolder() {
            return HotpotSoupComponentTypeSerializers.DYNAMIC_MOB_EFFECT_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER;
        }

        public int getSize() {
            return size;
        }
    }

    public static class Serializer implements IHotpotSoupComponentTypeSerializer<HotpotDynamicMobEffectContainerSoupComponent> {
        public static final MapCodec<Type> CODEC = Codec.INT.fieldOf("size").xmap(Type::new, Type::getSize);
        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = ByteBufCodecs.INT.<RegistryFriendlyByteBuf>cast().map(Type::new, Type::getSize);

        @Override
        public MapCodec<? extends IHotpotSoupComponentType<HotpotDynamicMobEffectContainerSoupComponent>> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupComponentType<HotpotDynamicMobEffectContainerSoupComponent>> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
