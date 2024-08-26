package com.github.argon4w.hotpot.soups.components.ticks;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import com.github.argon4w.hotpot.soups.components.HotpotSoupComponentTypeSerializers;
import com.github.argon4w.hotpot.soups.components.IHotpotSoupComponentType;
import com.github.argon4w.hotpot.soups.components.IHotpotSoupComponentTypeSerializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class HotpotDropPunishCooldownWhenNotEmptySoupComponent extends AbstractHotpotSoupComponent {
    private final int cooldownDropRate;

    public HotpotDropPunishCooldownWhenNotEmptySoupComponent(int cooldownDropRate) {
        this.cooldownDropRate = cooldownDropRate;
    }

    @Override
    public void onTick(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
        soup.getComponentsByType(HotpotSoupComponentTypeSerializers.PUNISH_COOLDOWN_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER).forEach(component -> component.setEmptyWaterPunishCooldown(soup.getWaterLevel() > 0 ? (component.getEmptyWaterPunishCooldown() - cooldownDropRate) : component.getEmptyWaterPunishCooldown()));
    }

    public static class Type implements IHotpotSoupComponentType<HotpotDropPunishCooldownWhenNotEmptySoupComponent> {
        private final int cooldownDropRate;
        private final HotpotDropPunishCooldownWhenNotEmptySoupComponent unit;

        private final MapCodec<HotpotDropPunishCooldownWhenNotEmptySoupComponent> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, HotpotDropPunishCooldownWhenNotEmptySoupComponent> streamCodec;

        public Type(int cooldownDropRate) {
            this.cooldownDropRate = cooldownDropRate;
            this.unit = new HotpotDropPunishCooldownWhenNotEmptySoupComponent(cooldownDropRate);

            this.codec = MapCodec.unit(unit);
            this.streamCodec = StreamCodec.unit(unit);
        }

        @Override
        public MapCodec<HotpotDropPunishCooldownWhenNotEmptySoupComponent> getCodec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotDropPunishCooldownWhenNotEmptySoupComponent> getStreamCodec() {
            return streamCodec;
        }

        @Override
        public HotpotDropPunishCooldownWhenNotEmptySoupComponent get() {
            return unit;
        }

        @Override
        public Holder<IHotpotSoupComponentTypeSerializer<?>> getSerializerHolder() {
            return HotpotSoupComponentTypeSerializers.DROP_PUNISH_COOLDOWN_WHEN_NOT_EMPTY_SOUP_COMPONENT_TYPE_SERIALIZER;
        }

        public int getCooldownDropRate() {
            return cooldownDropRate;
        }
    }

    public static class Serializer implements IHotpotSoupComponentTypeSerializer<HotpotDropPunishCooldownWhenNotEmptySoupComponent> {
        public static final MapCodec<Type> CODEC = Codec.INT.fieldOf("cooldown_drop_rate").xmap(Type::new, Type::getCooldownDropRate);
        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = ByteBufCodecs.INT.<RegistryFriendlyByteBuf>cast().map(Type::new, Type::getCooldownDropRate);

        @Override
        public MapCodec<? extends IHotpotSoupComponentType<HotpotDropPunishCooldownWhenNotEmptySoupComponent>> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupComponentType<HotpotDropPunishCooldownWhenNotEmptySoupComponent>> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
