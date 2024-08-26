package com.github.argon4w.hotpot.soups.components.ticks;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class HotpotIncreasePunishCooldownWhenEmptySoupComponent extends AbstractHotpotSoupComponent {
    private final int minCooldownWhenEmpty;
    private final int cooldownIncreaseRate;

    public HotpotIncreasePunishCooldownWhenEmptySoupComponent(int minCooldownWhenEmpty, int cooldownIncreaseRate) {
        this.minCooldownWhenEmpty = minCooldownWhenEmpty;
        this.cooldownIncreaseRate = cooldownIncreaseRate;
    }

    @Override
    public void onTick(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
        soup.getComponentsByType(HotpotSoupComponentTypeSerializers.PUNISH_COOLDOWN_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER).forEach(component -> component.setEmptyWaterPunishCooldown(soup.getWaterLevel() <= 0 ? (Math.max(minCooldownWhenEmpty, component.getEmptyWaterPunishCooldown()) + cooldownIncreaseRate) : component.getEmptyWaterPunishCooldown()));
    }

    public static class Type implements IHotpotSoupComponentType<HotpotIncreasePunishCooldownWhenEmptySoupComponent> {
        private final int minCooldownWhenEmpty;
        private final int cooldownIncreaseRate;
        private final HotpotIncreasePunishCooldownWhenEmptySoupComponent unit;

        private final MapCodec<HotpotIncreasePunishCooldownWhenEmptySoupComponent> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, HotpotIncreasePunishCooldownWhenEmptySoupComponent> streamCodec;

        public Type(int minCooldownWhenEmpty, int cooldownIncreaseRate) {
            this.minCooldownWhenEmpty = minCooldownWhenEmpty;
            this.cooldownIncreaseRate = cooldownIncreaseRate;
            this.unit = new HotpotIncreasePunishCooldownWhenEmptySoupComponent(minCooldownWhenEmpty, cooldownIncreaseRate);

            this.codec = MapCodec.unit(unit);
            this.streamCodec = StreamCodec.unit(unit);
        }

        @Override
        public MapCodec<HotpotIncreasePunishCooldownWhenEmptySoupComponent> getCodec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotIncreasePunishCooldownWhenEmptySoupComponent> getStreamCodec() {
            return streamCodec;
        }

        @Override
        public HotpotIncreasePunishCooldownWhenEmptySoupComponent get() {
            return unit;
        }

        @Override
        public Holder<IHotpotSoupComponentTypeSerializer<?>> getSerializerHolder() {
            return HotpotSoupComponentTypeSerializers.INCREASE_PUNISH_COOLDOWN_WHEN_EMPTY_SOUP_COMPONENT_TYPE_SERIALIZER;
        }

        public int getMinCooldownWhenEmpty() {
            return minCooldownWhenEmpty;
        }

        public int getCooldownIncreaseRate() {
            return cooldownIncreaseRate;
        }
    }

    public static class Serializer implements IHotpotSoupComponentTypeSerializer<HotpotIncreasePunishCooldownWhenEmptySoupComponent> {
        public static final MapCodec<Type> CODEC = RecordCodecBuilder.mapCodec(type -> type.group(
                Codec.INT.fieldOf("min_cooldown_when_empty").forGetter(Type::getMinCooldownWhenEmpty),
                Codec.INT.fieldOf("cooldown_increase_rate").forGetter(Type::getCooldownIncreaseRate)
        ).apply(type, Type::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, Type::getMinCooldownWhenEmpty,
                ByteBufCodecs.INT, Type::getCooldownIncreaseRate,
                Type::new
        );

        @Override
        public MapCodec<? extends IHotpotSoupComponentType<HotpotIncreasePunishCooldownWhenEmptySoupComponent>> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupComponentType<HotpotIncreasePunishCooldownWhenEmptySoupComponent>> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
