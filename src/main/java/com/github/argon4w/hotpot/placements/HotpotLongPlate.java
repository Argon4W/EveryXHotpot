package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.HotpotModEntry;
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

public class HotpotLongPlate implements IHotpotPlate {
    private final int position1;
    private final int position2;

    private final SimpleItemSlot itemSlot1;
    private final SimpleItemSlot itemSlot2;
    private final SimpleItemSlot plateItemSlot;

    public HotpotLongPlate(int position1, int position2) {
        this.position1 = position1;
        this.position2 = position2;
        this.itemSlot1 = new SimpleItemSlot();
        this.itemSlot2 = new SimpleItemSlot();
        this.plateItemSlot = new SimpleItemSlot();
    }

    public HotpotLongPlate(int position1, int position2, SimpleItemSlot itemSlot1, SimpleItemSlot itemSlot2, SimpleItemSlot plateItemSlot) {
        this.position1 = position1;
        this.position2 = position2;

        this.itemSlot1 = itemSlot1;
        this.itemSlot2 = itemSlot2;
        this.plateItemSlot = plateItemSlot;
    }

    @Override
    public void interact(Player player, InteractionHand hand, ItemStack itemStack, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container) {
        if (itemStack.isEmpty() && player.isCrouching() && container.canBeRemoved()) {
            onRemove(container, pos);
            return;
        }

        if (itemStack.isEmpty()) {
            pos.dropItemStack(getContent(player, hand, position, layer, pos, container, true));
            return;
        }

        if (itemStack.is(HotpotModEntry.HOTPOT_LONG_PLATE)) {
            plateItemSlot.addItem(itemStack);
            return;
        }

        SimpleItemSlot preferred = position == position1 ? itemSlot1 : itemSlot2;
        SimpleItemSlot fallback = position == position1 ? itemSlot2 : itemSlot1;

        if (!fallback.isEmpty() && fallback.addItem(itemStack)) {
            return;
        }

        if (!preferred.addItem(itemStack)) {
            fallback.addItem(itemStack);
        }
    }

    @Override
    public ItemStack getContent(Player player, InteractionHand hand, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container, boolean tableware) {
        boolean consume = container.consumeContents();

        if (position == position1 && !itemSlot1.isEmpty()) {
            return itemSlot1.takeItem(consume);
        }

        if (!itemSlot2.isEmpty()) {
            return itemSlot2.takeItem(consume);
        }

        return plateItemSlot.takeItem(consume);
    }

    @Override
    public void onRemove(IHotpotPlacementContainer container, LevelBlockPos pos) {
        plateItemSlot.dropItem(pos);
        itemSlot1.dropItem(pos);
        itemSlot2.dropItem(pos);
    }

    @Override
    public boolean shouldRemove(Player player, InteractionHand hand, ItemStack itemStack, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container) {
        return plateItemSlot.isEmpty() && container.canBeRemoved();
    }

    @Override
    public ItemStack getCloneItemStack(IHotpotPlacementContainer container, LevelBlockPos selfPos) {
        return plateItemSlot.getItemStack();
    }

    @Override
    public List<Integer> getPositions() {
        return List.of(position1, position2);
    }

    @Override
    public Holder<IHotpotPlacementSerializer<?>> getPlacementSerializerHolder() {
        return HotpotPlacementSerializers.LONG_PLATE_SERIALIZER;
    }

    @Override
    public void setPlateItemSlot(ItemStack itemStack) {
        plateItemSlot.set(itemStack);
    }

    public int getPosition1() {
        return position1;
    }

    public int getPosition2() {
        return position2;
    }

    public SimpleItemSlot getItemSlot1() {
        return itemSlot1;
    }

    public SimpleItemSlot getItemSlot2() {
        return itemSlot2;
    }

    public SimpleItemSlot getPlateItemSlot() {
        return plateItemSlot;
    }

    public static class Serializer implements IHotpotPlacementSerializer<HotpotLongPlate> {
        public static final MapCodec<HotpotLongPlate> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(plate -> plate.group(
                        Codec.INT.fieldOf("pos_1").forGetter(HotpotLongPlate::getPosition1),
                        Codec.INT.fieldOf("pos_2").forGetter(HotpotLongPlate::getPosition2),
                        SimpleItemSlot.CODEC.fieldOf("item_slot_1").forGetter(HotpotLongPlate::getItemSlot1),
                        SimpleItemSlot.CODEC.fieldOf("item_slot_2").forGetter(HotpotLongPlate::getItemSlot2),
                        SimpleItemSlot.CODEC.fieldOf("plate_item_slot").forGetter(HotpotLongPlate::getPlateItemSlot)
                ).apply(plate, HotpotLongPlate::new))
        );

        @Override
        public HotpotLongPlate get(List<Integer> positions, ComplexDirection direction) {
            return new HotpotLongPlate(positions.getFirst(), positions.get(1));
        }

        @Override
        public MapCodec<HotpotLongPlate> getCodec() {
            return CODEC;
        }

        @Override
        public List<Optional<Integer>> getPositions(int position, ComplexDirection direction) {
            return List.of(Optional.of(position), direction.relativeTo(position));
        }
    }
}
