package com.github.argon4w.hotpot.soups.components.entities;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class HotpotAddItemEntityInsideSoupComponent extends AbstractHotpotSoupComponent {
    private final boolean delayed;

    public HotpotAddItemEntityInsideSoupComponent(boolean delayed) {
        this.delayed = delayed;
    }

    @Override
    public void onEntityInside(Entity entity, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
        if (!(entity instanceof ItemEntity itemEntity)) {
            return;
        }

        if (delayed && itemEntity.hasPickUpDelay()) {
            return;
        }

        ItemStack stack = itemEntity.getItem();

        if (stack.isEmpty()) {
            return;
        }

        hotpotBlockEntity.setItemStackContentWhenEmpty(HotpotBlockEntity.getClickPosition(pos.pos(), itemEntity.position()), stack, pos);
        itemEntity.setItem(stack);
    }

    public static class Type implements IHotpotSoupComponentType<HotpotAddItemEntityInsideSoupComponent> {
        private final boolean delayed;
        private final HotpotAddItemEntityInsideSoupComponent unit;

        private final MapCodec<HotpotAddItemEntityInsideSoupComponent> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, HotpotAddItemEntityInsideSoupComponent> streamCodec;

        public Type(boolean delayed) {
            this.delayed = delayed;
            this.unit = new HotpotAddItemEntityInsideSoupComponent(delayed);

            this.codec = MapCodec.unit(unit);
            this.streamCodec = StreamCodec.unit(unit);
        }

        @Override
        public MapCodec<HotpotAddItemEntityInsideSoupComponent> getCodec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HotpotAddItemEntityInsideSoupComponent> getStreamCodec() {
            return streamCodec;
        }

        @Override
        public HotpotAddItemEntityInsideSoupComponent get() {
            return unit;
        }

        @Override
        public Holder<IHotpotSoupComponentTypeSerializer<?>> getSerializerHolder() {
            return HotpotSoupComponentTypeSerializers.ADD_ITEM_ENTITY_INSIDE_SOUP_COMPONENT_TYPE_SERIALIZER;
        }

        public boolean isDelayed() {
            return delayed;
        }
    }

    public static class Serializer implements IHotpotSoupComponentTypeSerializer<HotpotAddItemEntityInsideSoupComponent> {
        public static final MapCodec<Type> CODEC = Codec.BOOL.optionalFieldOf("delayed", false).xmap(Type::new, Type::isDelayed);
        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = ByteBufCodecs.BOOL.<RegistryFriendlyByteBuf>cast().map(Type::new, Type::isDelayed);

        @Override
        public MapCodec<? extends IHotpotSoupComponentType<HotpotAddItemEntityInsideSoupComponent>> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends IHotpotSoupComponentType<HotpotAddItemEntityInsideSoupComponent>> getStreamCodec() {
            return STREAM_CODEC;
        }
    }
}

