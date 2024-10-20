package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.SimpleItemSlot;
import com.github.argon4w.hotpot.api.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.api.placements.IHotpotPlacementSerializer;
import com.github.argon4w.hotpot.api.placements.IHotpotCommonPlacement;
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

public class HotpotSmallPlate implements IHotpotCommonPlacement {
    private final int position;
    private final ComplexDirection direction;
    private final SimpleItemSlot itemSlot;
    private final SimpleItemSlot plateItemSlot;

    public HotpotSmallPlate(int position, ComplexDirection direction) {
        this.position = position;
        this.direction = direction;
        this.itemSlot = new SimpleItemSlot();
        this.plateItemSlot = new SimpleItemSlot();
    }

    public HotpotSmallPlate(int position, ComplexDirection direction, SimpleItemSlot itemSlot, SimpleItemSlot plateItemSlot) {
        this.position = position;
        this.direction = direction;
        this.itemSlot = itemSlot;
        this.plateItemSlot = plateItemSlot;
    }

    @Override
    public void interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainer container) {
        if (itemStack.isEmpty() && player.isCrouching() && container.canBeRemoved()) {
            onRemove(container, selfPos);
            return;
        }

        if (itemStack.isEmpty()) {
            selfPos.dropItemStack(getContent(player, hand, pos, layer, selfPos, container, false));
            return;
        }

        if (itemStack.is(HotpotModEntry.HOTPOT_SMALL_PLATE)) {
            plateItemSlot.addItem(itemStack);
            return;
        }

        itemSlot.addItem(itemStack);
    }

    @Override
    public void onRemove(IHotpotPlacementContainer container, LevelBlockPos pos) {
        plateItemSlot.dropItem(pos);
        itemSlot.dropItem(pos);
    }

    @Override
    public boolean shouldRemove(Player player, InteractionHand hand, ItemStack itemStack, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainer container) {
        return plateItemSlot.isEmpty() && container.canBeRemoved();
    }

    @Override
    public ItemStack getContent(Player player, InteractionHand hand, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainer container, boolean tableware) {
        return (itemSlot.isEmpty() ? plateItemSlot : itemSlot).takeItem(container.canConsumeContents());
    }

    @Override
    public ItemStack getCloneItemStack(IHotpotPlacementContainer container, LevelBlockPos selfPos) {
        return plateItemSlot.getItemStack();
    }

    @Override
    public List<Integer> getPositions() {
        return List.of(position);
    }

    @Override
    public Holder<IHotpotPlacementSerializer<?>> getPlacementSerializerHolder() {
        return HotpotPlacementSerializers.SMALL_PLATE_SERIALIZER;
    }

    @Override
    public void setCommonItemSlot(ItemStack itemStack) {
        plateItemSlot.set(itemStack);
    }

    public int getPosition() {
        return position;
    }

    public ComplexDirection getDirection() {
        return direction;
    }

    public SimpleItemSlot getItemSlot() {
        return itemSlot;
    }

    public SimpleItemSlot getPlateItemSlot() {
        return plateItemSlot;
    }

    public static class Serializer implements IHotpotPlacementSerializer<HotpotSmallPlate> {
        public static final MapCodec<HotpotSmallPlate> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(plate -> plate.group(
                        Codec.INT.fieldOf("pos").forGetter(HotpotSmallPlate::getPosition),
                        ComplexDirection.CODEC.fieldOf("direction").forGetter(HotpotSmallPlate::getDirection),
                        SimpleItemSlot.CODEC.fieldOf("item_slot").forGetter(HotpotSmallPlate::getItemSlot),
                        SimpleItemSlot.CODEC.fieldOf("plate_item_slot").forGetter(HotpotSmallPlate::getPlateItemSlot)
                ).apply(plate, HotpotSmallPlate::new))
        );

        @Override
        public HotpotSmallPlate createPlacement(List<Integer> positions, ComplexDirection direction) {
            return new HotpotSmallPlate(positions.getFirst(), direction);
        }

        @Override
        public MapCodec<HotpotSmallPlate> getCodec() {
            return CODEC;
        }

        @Override
        public List<Optional<Integer>> getPositions(int position, ComplexDirection direction) {
            return List.of(Optional.of(position));
        }
    }
}
