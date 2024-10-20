package com.github.argon4w.hotpot.soups.components.containers;

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

public class HotpotPunishCooldownContainerSoupComponent extends AbstractHotpotSoupComponent {
    private int emptyWaterPunishCooldown;
    private final int minCooldown;
    private final int maxCooldown;

    public HotpotPunishCooldownContainerSoupComponent(int minCooldown, int maxCooldown) {
        this.minCooldown = minCooldown;
        this.maxCooldown = maxCooldown;
        this.emptyWaterPunishCooldown = 0;
    }

    public HotpotPunishCooldownContainerSoupComponent(int minCooldown, int maxCooldown, int emptyWaterPunishCooldown) {
        this.minCooldown = minCooldown;
        this.maxCooldown = maxCooldown;
        this.emptyWaterPunishCooldown = emptyWaterPunishCooldown;
    }

    public void setEmptyWaterPunishCooldown(int emptyWaterPunishCooldown) {
        this.emptyWaterPunishCooldown = Math.clamp(emptyWaterPunishCooldown, minCooldown, maxCooldown);
    }

    public int getEmptyWaterPunishCooldown() {
        return emptyWaterPunishCooldown;
    }

    @Override
    public boolean shouldSendToClient() {
        return true;
    }

    public static class Type implements IHotpotSoupComponentType<HotpotPunishCooldownContainerSoupComponent> {
        private final int minCooldown;
        private final int maxCooldown;

        private final MapCodec<HotpotPunishCooldownContainerSoupComponent> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, HotpotPunishCooldownContainerSoupComponent> streamCodec;

        public Type(int minCooldown, int maxCooldown) {
            this.minCooldown = minCooldown;
            this.maxCooldown = maxCooldown;

            this.codec = Codec.INT.fieldOf("empty_water_punish_cooldown").xmap(emptyWaterPunishCooldown -> new HotpotPunishCooldownContainerSoupComponent(minCooldown, maxCooldown, emptyWaterPunishCooldown), HotpotPunishCooldownContainerSoupComponent::getEmptyWaterPunishCooldown);
            this.streamCodec = ByteBufCodecs.INT.<RegistryFriendlyByteBuf>cast().map(emptyWaterPunishCooldown -> new HotpotPunishCooldownContainerSoupComponent(minCooldown, maxCooldown, emptyWaterPunishCooldown), HotpotPunishCooldownContainerSoupComponent::getEmptyWaterPunishCooldown);
        }

        @Override
        public MapCodec<HotpotPunishCooldownContainerSoupComponent> getCodec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotPunishCooldownContainerSoupComponent> getStreamCodec() {
            return streamCodec;
        }

        @Override
        public HotpotPunishCooldownContainerSoupComponent createSoupComponent() {
            return new HotpotPunishCooldownContainerSoupComponent(minCooldown, maxCooldown);
        }

        @Override
        public Holder<IHotpotSoupComponentTypeSerializer<?>> getSerializerHolder() {
            return HotpotSoupComponentTypeSerializers.PUNISH_COOLDOWN_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER;
        }

        public int getMinCooldown() {
            return minCooldown;
        }

        public int getMaxCooldown() {
            return maxCooldown;
        }
    }

    public static class Serializer implements IHotpotSoupComponentTypeSerializer<HotpotPunishCooldownContainerSoupComponent> {
        public static final MapCodec<Type> CODEC = RecordCodecBuilder.mapCodec(type -> type.group(
                Codec.INT.optionalFieldOf("min_cooldown", 0).forGetter(Type::getMinCooldown),
                Codec.INT.fieldOf("max_cooldown").forGetter(Type::getMaxCooldown)
        ).apply(type, Type::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, Type::getMinCooldown,
                ByteBufCodecs.INT, Type::getMaxCooldown,
                Type::new
        );

        @Override
        public MapCodec<? extends IHotpotSoupComponentType<HotpotPunishCooldownContainerSoupComponent>> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupComponentType<HotpotPunishCooldownContainerSoupComponent>> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
