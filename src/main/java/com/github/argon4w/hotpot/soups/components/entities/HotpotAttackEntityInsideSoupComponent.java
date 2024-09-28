package com.github.argon4w.hotpot.soups.components.entities;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.soups.components.IHotpotDamageSource;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponentType;
import com.github.argon4w.hotpot.api.soups.components.IHotpotSoupComponentTypeSerializer;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.codecs.LazyMapCodec;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public class HotpotAttackEntityInsideSoupComponent extends AbstractHotpotSoupComponent {
    private final IHotpotDamageSource.Wrapper damageSourceWrapper;

    public HotpotAttackEntityInsideSoupComponent(IHotpotDamageSource.Wrapper damageSourceWrapper) {
        this.damageSourceWrapper = damageSourceWrapper;
    }

    @Override
    public void onEntityInside(Entity entity, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
        if (entity.isAttackable()) {
            damageSourceWrapper.hurt(entity, pos.toVec3());
        }
    }

    public static class Type implements IHotpotSoupComponentType<HotpotAttackEntityInsideSoupComponent> {
        private final IHotpotDamageSource.Wrapper damageSourceWrapper;
        private final HotpotAttackEntityInsideSoupComponent unit;

        private final MapCodec<HotpotAttackEntityInsideSoupComponent> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, HotpotAttackEntityInsideSoupComponent> streamCodec;

        public Type(IHotpotDamageSource.Wrapper damageSourceWrapper) {
            this.damageSourceWrapper = damageSourceWrapper;
            this.unit = new HotpotAttackEntityInsideSoupComponent(damageSourceWrapper);

            this.codec = MapCodec.unit(unit);
            this.streamCodec = StreamCodec.unit(unit);
        }

        @Override
        public MapCodec<HotpotAttackEntityInsideSoupComponent> getCodec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotAttackEntityInsideSoupComponent> getStreamCodec() {
            return streamCodec;
        }

        @Override
        public HotpotAttackEntityInsideSoupComponent get() {
            return unit;
        }

        @Override
        public Holder<IHotpotSoupComponentTypeSerializer<?>> getSerializerHolder() {
            return HotpotSoupComponentTypeSerializers.ATTACK_ENTITY_INSIDE_SOUP_COMPONENT_TYPE_SERIALIZER;
        }

        public IHotpotDamageSource.Wrapper getDamageSourceWrapper() {
            return damageSourceWrapper;
        }
    }

    public static class Serializer implements IHotpotSoupComponentTypeSerializer<HotpotAttackEntityInsideSoupComponent> {
        private static final MapCodec<Type> CODEC = LazyMapCodec.of(() -> IHotpotDamageSource.CODEC.optionalFieldOf("damage_source", IHotpotDamageSource.EMPTY).xmap(Type::new, Type::getDamageSourceWrapper));
        private static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> IHotpotDamageSource.STREAM_CODEC.map(Type::new, Type::getDamageSourceWrapper));

        @Override
        public MapCodec<? extends IHotpotSoupComponentType<HotpotAttackEntityInsideSoupComponent>> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupComponentType<HotpotAttackEntityInsideSoupComponent>> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}
