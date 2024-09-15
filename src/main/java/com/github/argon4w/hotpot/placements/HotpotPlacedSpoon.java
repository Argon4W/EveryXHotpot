package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.SimpleItemSlot;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainer;
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

public class HotpotPlacedSpoon implements IHotpotPlacement {
    private final int position1;
    private final int position2;
    private final SimpleItemSlot spoonItemSlot;

    public HotpotPlacedSpoon(int position1, int position2) {
        this.position1 = position1;
        this.position2 = position2;
        this.spoonItemSlot = new SimpleItemSlot();
    }

    public HotpotPlacedSpoon(int position1, int position2, SimpleItemSlot spoonItemSlot) {
        this.position1 = position1;
        this.position2 = position2;
        this.spoonItemSlot = spoonItemSlot;
    }

    @Override
    public void interact(Player player, InteractionHand hand, ItemStack itemStack, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container) {
        onRemove(container, pos);
    }

    @Override
    public ItemStack getContent(Player player, InteractionHand hand, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container, boolean tableware) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean shouldRemove(Player player, InteractionHand hand, ItemStack itemStack, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container) {
        return spoonItemSlot.isEmpty() && container.canBeRemoved();
    }

    @Override
    public void onRemove(IHotpotPlacementContainer container, LevelBlockPos pos) {
        spoonItemSlot.dropItem(pos);
    }

    @Override
    public ItemStack getCloneItemStack(IHotpotPlacementContainer container, LevelBlockPos pos) {
        return spoonItemSlot.getItemStack();
    }

    @Override
    public List<Integer> getPositions() {
        return List.of(position1, position2);
    }

    @Override
    public Holder<IHotpotPlacementSerializer<?>> getPlacementSerializerHolder() {
        return HotpotPlacementSerializers.PLACED_SPOON_SERIALIZER;
    }

    public void setSpoonItemSlot(ItemStack spoonItemSlot) {
        this.spoonItemSlot.set(spoonItemSlot);
    }

    public int getPosition1() {
        return position1;
    }

    public int getPosition2() {
        return position2;
    }

    public SimpleItemSlot getSpoonItemSlot() {
        return spoonItemSlot;
    }

    public static class Serializer implements IHotpotPlacementSerializer<HotpotPlacedSpoon> {
        public static final MapCodec<HotpotPlacedSpoon> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(spoon -> spoon.group(
                        Codec.INT.fieldOf("pos_1").forGetter(HotpotPlacedSpoon::getPosition1),
                        Codec.INT.fieldOf("pos_2").forGetter(HotpotPlacedSpoon::getPosition2),
                        SimpleItemSlot.CODEC.fieldOf("spoon_item_slot").forGetter(HotpotPlacedSpoon::getSpoonItemSlot)
                ).apply(spoon, HotpotPlacedSpoon::new))
        );

        @Override
        public HotpotPlacedSpoon get(List<Integer> positions, ComplexDirection direction) {
            return new HotpotPlacedSpoon(positions.getFirst(), positions.get(1));
        }

        @Override
        public MapCodec<HotpotPlacedSpoon> getCodec() {
            return CODEC;
        }

        @Override
        public List<Optional<Integer>> getPositions(int position, ComplexDirection direction) {
            return List.of(Optional.of(position), direction.relativeTo(position));
        }
    }
}
