package com.github.argon4w.hotpot.soups.components.containers;

import com.github.argon4w.fancytoys.MobEffectMap;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponentType;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponentTypeSerializer;
import com.github.argon4w.fancytoys.codecs.LazyMapCodec;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import com.github.argon4w.hotpot.soups.components.HotpotSoupComponentTypeSerializers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public class HotpotDynamicMobEffectContainerSoupComponent
        extends AbstractHotpotSoupComponent
        implements IHotpotMobEffectContainerSoupComponent {

    private final MobEffectMap.Sized mobEffectMap;
    private final MobEffectMap.Sized scheduledMobEffectMap;

    public HotpotDynamicMobEffectContainerSoupComponent(MobEffectMap.Sized mobEffectMap, MobEffectMap.Sized scheduledMobEffectMap) {
        this.mobEffectMap = mobEffectMap;
        this.scheduledMobEffectMap = scheduledMobEffectMap;
    }

    public HotpotDynamicMobEffectContainerSoupComponent(int size) {
        this.mobEffectMap = new MobEffectMap.Sized(size);
        this.scheduledMobEffectMap = new MobEffectMap.Sized(size);
    }

    public void putEffects(MobEffectMap mobEffectMap) {
        this.mobEffectMap.putEffects(mobEffectMap);
    }

    public void putScheduledEffect(MobEffectInstance mobEffectInstance) {
        scheduledMobEffectMap.putEffect(mobEffectInstance);
    }

    public void clearScheduledEffects() {
        scheduledMobEffectMap.clear();
    }

    public boolean isScheduled() {
        return !scheduledMobEffectMap.isEmpty();
    }

    public MobEffectMap.Sized getScheduledMobEffectMap() {
        return scheduledMobEffectMap;
    }

    @Override
    public MobEffectMap.Sized getMobEffectMap() {
        return mobEffectMap.copy();
    }

    public static class Type implements IHotpotSoupComponentType<HotpotDynamicMobEffectContainerSoupComponent> {

        private final int size;

        private final Codec<MobEffectMap.Sized> sizedMobEffectMapCodec;
        private final StreamCodec<RegistryFriendlyByteBuf, MobEffectMap.Sized> sizedMobEffectMapStreamCodec;

        private final MapCodec<HotpotDynamicMobEffectContainerSoupComponent> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, HotpotDynamicMobEffectContainerSoupComponent> streamCodec;

        public Type(int size) {
            this.size = size;

            this.sizedMobEffectMapCodec = MobEffectMap.getCodec(size);
            this.sizedMobEffectMapStreamCodec = MobEffectMap.getStreamCodec(size);

            this.codec = LazyMapCodec.of(() -> RecordCodecBuilder.mapCodec(component -> component.group(
                    sizedMobEffectMapCodec.fieldOf("effects").forGetter(HotpotDynamicMobEffectContainerSoupComponent::getMobEffectMap),
                    sizedMobEffectMapCodec.fieldOf("scheduled_effects").forGetter(HotpotDynamicMobEffectContainerSoupComponent::getScheduledMobEffectMap)
            ).apply(component, HotpotDynamicMobEffectContainerSoupComponent::new)));

            this.streamCodec = NeoForgeStreamCodecs.lazy(() -> StreamCodec.composite(
                    sizedMobEffectMapStreamCodec, HotpotDynamicMobEffectContainerSoupComponent::getMobEffectMap,
                    sizedMobEffectMapStreamCodec, HotpotDynamicMobEffectContainerSoupComponent::getScheduledMobEffectMap,
                    HotpotDynamicMobEffectContainerSoupComponent::new));
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
        public HotpotDynamicMobEffectContainerSoupComponent createSoupComponent() {
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
        public static final MapCodec<Type> CODEC = Codec.INT
                .fieldOf("size")
                .xmap(Type::new, Type::getSize);

        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = ByteBufCodecs.INT
                .<RegistryFriendlyByteBuf>cast()
                .map(Type::new, Type::getSize);

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
