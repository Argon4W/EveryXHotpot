package com.github.argon4w.hotpot.soups.components;

import com.github.argon4w.hotpot.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.codecs.LazyMapCodec;
import com.github.argon4w.hotpot.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public class HotpotHurtPlayerWHenInteractSoupComponent extends AbstractHotpotSoupComponent {
    private final IHotpotDamageSource.Wrapper damageSourceWrapper;

    public HotpotHurtPlayerWHenInteractSoupComponent(IHotpotDamageSource.Wrapper damageSourceWrapper) {
        this.damageSourceWrapper = damageSourceWrapper;
    }

    @Override
    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getPlayerInteractionResult(int position, Player player, InteractionHand hand, ItemStack itemStack, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<Holder<IHotpotContentSerializer<?>>> result, HotpotBlockEntity hotpotBlockEntity) {
        if (result.isPresent()) {
            return result;
        }

        if (!itemStack.isEmpty()) {
            return result;
        }

        damageSourceWrapper.hurt(player);
        return IHotpotResult.pass();
    }

    public static class Type implements IHotpotSoupComponentType<HotpotHurtPlayerWHenInteractSoupComponent> {
        private final IHotpotDamageSource.Wrapper damageSourceWrapper;
        private final HotpotHurtPlayerWHenInteractSoupComponent unit;

        private final MapCodec<HotpotHurtPlayerWHenInteractSoupComponent> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, HotpotHurtPlayerWHenInteractSoupComponent> streamCodec;

        public Type(IHotpotDamageSource.Wrapper damageSourceWrapper) {
            this.damageSourceWrapper = damageSourceWrapper;
            this.unit = new HotpotHurtPlayerWHenInteractSoupComponent(damageSourceWrapper);

            this.codec = MapCodec.unit(unit);
            this.streamCodec = StreamCodec.unit(unit);
        }

        @Override
        public MapCodec<HotpotHurtPlayerWHenInteractSoupComponent> getCodec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotHurtPlayerWHenInteractSoupComponent> getStreamCodec() {
            return streamCodec;
        }

        @Override
        public HotpotHurtPlayerWHenInteractSoupComponent get() {
            return unit;
        }

        @Override
        public Holder<IHotpotSoupComponentTypeSerializer<?>> getSerializerHolder() {
            return HotpotSoupComponentTypeSerializers.HURT_PLAYER_WHEN_INTERACT_SOUP_COMPONENT_TYPE_SERIALIZER;
        }

        public IHotpotDamageSource.Wrapper getDamageSourceWrapper() {
            return damageSourceWrapper;
        }
    }

    public static class Serializer implements IHotpotSoupComponentTypeSerializer<HotpotHurtPlayerWHenInteractSoupComponent> {
        public static final MapCodec<Type> CODEC = LazyMapCodec.of(() -> IHotpotDamageSource.CODEC.optionalFieldOf("damage_source", IHotpotDamageSource.EMPTY).xmap(Type::new, Type::getDamageSourceWrapper));
        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> IHotpotDamageSource.STREAM_CODEC.map(Type::new, Type::getDamageSourceWrapper));

        @Override
        public MapCodec<? extends IHotpotSoupComponentType<HotpotHurtPlayerWHenInteractSoupComponent>> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupComponentType<HotpotHurtPlayerWHenInteractSoupComponent>> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}

