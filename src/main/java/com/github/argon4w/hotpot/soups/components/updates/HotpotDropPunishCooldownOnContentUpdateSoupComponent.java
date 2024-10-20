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
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class HotpotDropPunishCooldownOnContentUpdateSoupComponent extends AbstractHotpotSoupComponent {
    private final int droppedCooldown;

    public HotpotDropPunishCooldownOnContentUpdateSoupComponent(int droppedCooldown) {
        this.droppedCooldown = droppedCooldown;
    }

    @Override
    public IHotpotResult<IHotpotContent> onContentUpdate(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<IHotpotContent> result) {
        return result.ifPresent(content -> soup.getComponentsByType(HotpotSoupComponentTypeSerializers.PUNISH_COOLDOWN_CONTAINER_SOUP_COMPONENT_TYPE_SERIALIZER).forEach(component -> component.setEmptyWaterPunishCooldown(component.getEmptyWaterPunishCooldown() - droppedCooldown)));
    }

    public static class Type implements IHotpotSoupComponentType<HotpotDropPunishCooldownOnContentUpdateSoupComponent> {
        private final int droppedCooldown;
        private final HotpotDropPunishCooldownOnContentUpdateSoupComponent unit;

        private final MapCodec<HotpotDropPunishCooldownOnContentUpdateSoupComponent> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, HotpotDropPunishCooldownOnContentUpdateSoupComponent> streamCodec;

        public Type(int droppedCooldown) {
            this.droppedCooldown = droppedCooldown;
            this.unit = new HotpotDropPunishCooldownOnContentUpdateSoupComponent(droppedCooldown);

            this.codec = MapCodec.unit(unit);
            this.streamCodec = StreamCodec.unit(unit);
        }

        @Override
        public MapCodec<HotpotDropPunishCooldownOnContentUpdateSoupComponent> getCodec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotDropPunishCooldownOnContentUpdateSoupComponent> getStreamCodec() {
            return streamCodec;
        }

        @Override
        public HotpotDropPunishCooldownOnContentUpdateSoupComponent createSoupComponent() {
            return unit;
        }

        @Override
        public Holder<IHotpotSoupComponentTypeSerializer<?>> getSerializerHolder() {
            return HotpotSoupComponentTypeSerializers.DROP_PUNISH_COOLDOWN_ON_CONTENT_UPDATE_SOUP_COMPONENT_TYPE_SERIALIZER;
        }

        public int getDroppedCooldown() {
            return droppedCooldown;
        }
    }

    public static class Serializer implements IHotpotSoupComponentTypeSerializer<HotpotDropPunishCooldownOnContentUpdateSoupComponent> {
        public static final MapCodec<Type> CODEC = Codec.INT.fieldOf("dropped_cooldown").xmap(Type::new, Type::getDroppedCooldown);
        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = ByteBufCodecs.INT.<RegistryFriendlyByteBuf>cast().map(Type::new, Type::getDroppedCooldown);

        @Override
        public MapCodec<? extends IHotpotSoupComponentType<HotpotDropPunishCooldownOnContentUpdateSoupComponent>> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupComponentType<HotpotDropPunishCooldownOnContentUpdateSoupComponent>> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
