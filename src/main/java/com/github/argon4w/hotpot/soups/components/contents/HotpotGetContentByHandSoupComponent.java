package com.github.argon4w.hotpot.soups.components.contents;

import com.github.argon4w.hotpot.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.codecs.LazyMapCodec;
import com.github.argon4w.hotpot.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public class HotpotGetContentByHandSoupComponent extends AbstractHotpotSoupComponent {
    private final IHotpotDamageSource.Wrapper damageSourceWrapper;

    public HotpotGetContentByHandSoupComponent(IHotpotDamageSource.Wrapper damageSourceWrapper) {
        this.damageSourceWrapper = damageSourceWrapper;
    }

    @Override
    public IHotpotResult<Holder<IHotpotContentSerializer<?>>> getPlayerInteractionResult(int hitPos, Player player, InteractionHand hand, ItemStack itemStack, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<Holder<IHotpotContentSerializer<?>>> result, HotpotBlockEntity hotpotBlockEntity) {
        if (result.isPresent()) {
            return result;
        }

        if (!itemStack.isEmpty()) {
            return result;
        }

        damageSourceWrapper.hurt(player);
        hotpotBlockEntity.getContentByHand(hitPos, pos);

        return IHotpotResult.blocked();
    }

    public static class Type implements IHotpotSoupComponentType<HotpotGetContentByHandSoupComponent> {
        private final IHotpotDamageSource.Wrapper damageSourceWrapper;
        private final HotpotGetContentByHandSoupComponent unit;

        private final MapCodec<HotpotGetContentByHandSoupComponent> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, HotpotGetContentByHandSoupComponent> streamCodec;

        public Type(IHotpotDamageSource.Wrapper damageSourceWrapper) {
            this.damageSourceWrapper = damageSourceWrapper;
            this.unit = new HotpotGetContentByHandSoupComponent(damageSourceWrapper);

            this.codec = MapCodec.unit(unit);
            this.streamCodec = StreamCodec.unit(unit);
        }

        @Override
        public MapCodec<HotpotGetContentByHandSoupComponent> getCodec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotGetContentByHandSoupComponent> getStreamCodec() {
            return streamCodec;
        }

        @Override
        public HotpotGetContentByHandSoupComponent get() {
            return unit;
        }

        @Override
        public Holder<IHotpotSoupComponentTypeSerializer<?>> getSerializerHolder() {
            return HotpotSoupComponentTypeSerializers.GET_CONTENT_BY_HAND_SOUP_COMPONENT_TYPE_SERIALIZER;
        }

        public IHotpotDamageSource.Wrapper getDamageSourceWrapper() {
            return damageSourceWrapper;
        }
    }

    public static class Serializer implements IHotpotSoupComponentTypeSerializer<HotpotGetContentByHandSoupComponent> {
        public static final MapCodec<Type> CODEC = LazyMapCodec.of(() -> IHotpotDamageSource.CODEC.optionalFieldOf("damage_source", IHotpotDamageSource.EMPTY).xmap(Type::new, Type::getDamageSourceWrapper));
        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> IHotpotDamageSource.STREAM_CODEC.map(Type::new, Type::getDamageSourceWrapper));

        @Override
        public MapCodec<? extends IHotpotSoupComponentType<HotpotGetContentByHandSoupComponent>> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupComponentType<HotpotGetContentByHandSoupComponent>> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}

