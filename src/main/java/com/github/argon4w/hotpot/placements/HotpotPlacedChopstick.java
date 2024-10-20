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
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class HotpotPlacedChopstick implements IHotpotPlacement {
    private final int position1;
    private final int position2;
    private final SimpleItemSlot chopstickItemSlot;

    public HotpotPlacedChopstick(int position1, int position2) {
        this.position1 = position1;
        this.position2 = position2;
        this.chopstickItemSlot = new SimpleItemSlot();
    }

    public HotpotPlacedChopstick(int position1, int position2, SimpleItemSlot chopstickItemSlot) {
        this.position1 = position1;
        this.position2 = position2;
        this.chopstickItemSlot = chopstickItemSlot;
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
        chopstickItemSlot.dropItem(pos);
    }

    @Override
    public boolean shouldRemove(Player player, InteractionHand hand, ItemStack itemStack, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container) {
        return chopstickItemSlot.isEmpty() && container.canBeRemoved();
    }

    @Override
    public ItemStack getCloneItemStack(IHotpotPlacementContainer container, LevelBlockPos pos) {
        return chopstickItemSlot.getItemStack();
    }

    @Override
    public List<Integer> getPositions() {
        return List.of(position1, position2);
    }

    @Override
    public Holder<IHotpotPlacementSerializer<?>> getPlacementSerializerHolder() {
        return HotpotPlacementSerializers.PLACED_CHOPSTICK_SERIALIZER;
    }

    public void setChopstickItemSlot(ItemStack chopstickItemSlot) {
        this.chopstickItemSlot.set(chopstickItemSlot);
    }

    public int getPosition1() {
        return position1;
    }

    public int getPosition2() {
        return position2;
    }

    public SimpleItemSlot getChopstickItemSlot() {
        return chopstickItemSlot;
    }

    public static class Serializer implements IHotpotPlacementSerializer<HotpotPlacedChopstick> {
        public static final MapCodec<HotpotPlacedChopstick> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(chopstick -> chopstick.group(
                        Codec.INT.fieldOf("pos_1").forGetter(HotpotPlacedChopstick::getPosition1),
                        Codec.INT.fieldOf("pos_2").forGetter(HotpotPlacedChopstick::getPosition2),
                        SimpleItemSlot.CODEC.fieldOf("chopstick_item_slot").forGetter(HotpotPlacedChopstick::getChopstickItemSlot)
                ).apply(chopstick, HotpotPlacedChopstick::new))
        );

        @Override
        public HotpotPlacedChopstick createPlacement(List<Integer> positions, ComplexDirection direction) {
            return new HotpotPlacedChopstick(positions.get(0), positions.get(1));
        }

        @Override
        public MapCodec<HotpotPlacedChopstick> getCodec() {
            return CODEC;
        }

        @Override
        public List<Optional<Integer>> getPositions(int position, ComplexDirection direction) {
            return List.of(Optional.of(position), direction.relativeTo(position));
        }
    }
}
