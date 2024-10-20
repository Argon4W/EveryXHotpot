package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.SimpleItemSlot;
import com.github.argon4w.hotpot.api.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.api.placements.IHotpotPlacement;
import com.github.argon4w.hotpot.api.placements.IHotpotPlacementSerializer;
import com.github.argon4w.hotpot.codecs.LazyMapCodec;
import com.github.argon4w.hotpot.placements.coords.ComplexDirection;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class HotpotPlacedStrainerBasket implements IHotpotPlacement {
    private final int position;
    private final ComplexDirection direction;
    private final SimpleItemSlot strainerBasketItemSlot;

    public HotpotPlacedStrainerBasket(int position, ComplexDirection direction) {
        this.position = position;
        this.direction = direction;
        this.strainerBasketItemSlot = new SimpleItemSlot();
    }

    public HotpotPlacedStrainerBasket(int position, ComplexDirection direction, SimpleItemSlot strainerBasketItemSlot) {
        this.position = position;
        this.direction = direction;
        this.strainerBasketItemSlot = strainerBasketItemSlot;
    }

    @Override
    public void interact(Player player, InteractionHand hand, ItemStack itemStack, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container) {
        if (container.canBeRemoved()) {
            onRemove(container, pos);
        }
    }

    @Override
    public ItemStack getContent(Player player, InteractionHand hand, int position, int layer, LevelBlockPos seposfPos, IHotpotPlacementContainer container, boolean tableware) {
        return ItemStack.EMPTY;
    }

    @Override
    public void onRemove(IHotpotPlacementContainer container, LevelBlockPos pos) {
        strainerBasketItemSlot.dropItem(pos);
    }

    @Override
    public boolean shouldRemove(Player player, InteractionHand hand, ItemStack itemStack, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container) {
        return strainerBasketItemSlot.isEmpty() && container.canBeRemoved();
    }

    @Override
    public ItemStack getCloneItemStack(IHotpotPlacementContainer container, LevelBlockPos pos) {
        return strainerBasketItemSlot.getItemStack();
    }

    @Override
    public List<Integer> getPositions() {
        return List.of(position);
    }

    @Override
    public Holder<IHotpotPlacementSerializer<?>> getPlacementSerializerHolder() {
        return HotpotPlacementSerializers.PLACED_STRAINER_BASKET_SERIALIZER;
    }

    public void setStrainerBasketItemSlot(ItemStack strainerBasketItemSlot) {
        this.strainerBasketItemSlot.set(strainerBasketItemSlot);
    }

    public int getPosition() {
        return position;
    }

    public ComplexDirection getDirection() {
        return direction;
    }

    public SimpleItemSlot getStrainerBasketItemSlot() {
        return strainerBasketItemSlot;
    }

    public static class Serializer implements IHotpotPlacementSerializer<HotpotPlacedStrainerBasket> {
        public static final MapCodec<HotpotPlacedStrainerBasket> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(chopstick -> chopstick.group(
                        Codec.INT.fieldOf("pos").forGetter(HotpotPlacedStrainerBasket::getPosition),
                        ComplexDirection.CODEC.fieldOf("direction").forGetter(HotpotPlacedStrainerBasket::getDirection),
                        SimpleItemSlot.CODEC.fieldOf("strainer_basket_item_slot").forGetter(HotpotPlacedStrainerBasket::getStrainerBasketItemSlot)
                ).apply(chopstick, HotpotPlacedStrainerBasket::new))
        );

        @Override
        public HotpotPlacedStrainerBasket createPlacement(List<Integer> positions, ComplexDirection direction) {
            return new HotpotPlacedStrainerBasket(positions.getFirst(), direction);
        }

        @Override
        public MapCodec<HotpotPlacedStrainerBasket> getCodec() {
            return CODEC;
        }

        @Override
        public List<Optional<Integer>> getPositions(int position, ComplexDirection direction) {
            return List.of(Optional.of(position));
        }
    }
}
