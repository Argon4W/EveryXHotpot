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
import java.util.Map;
import java.util.Optional;

public class HotpotLargeRoundPlate implements IHotpotCommonPlacement {
    private final SimpleItemSlot itemSlot1;
    private final SimpleItemSlot itemSlot2;
    private final SimpleItemSlot itemSlot3;
    private final SimpleItemSlot itemSlot4;
    private final SimpleItemSlot plateItemSlot;

    private final int position1;
    private final int position2;
    private final int position3;
    private final int position4;

    private final Map<Integer, SimpleItemSlot> itemSlots;

    public HotpotLargeRoundPlate(int position1, int position2, int position3, int position4) {
        this.itemSlot1 = new SimpleItemSlot();
        this.itemSlot2 = new SimpleItemSlot();
        this.itemSlot3 = new SimpleItemSlot();
        this.itemSlot4 = new SimpleItemSlot();
        this.plateItemSlot = new SimpleItemSlot();

        this.position1 = position1;
        this.position2 = position2;
        this.position3 = position3;
        this.position4 = position4;

        this.itemSlots = Map.of(
                this.position1, this.itemSlot1,
                this.position2, this.itemSlot2,
                this.position3, this.itemSlot3,
                this.position4, this.itemSlot4
        );
    }

    public HotpotLargeRoundPlate(SimpleItemSlot itemSlot1, SimpleItemSlot itemSlot2, SimpleItemSlot itemSlot3, SimpleItemSlot itemSlot4, SimpleItemSlot plateItemSlot, int position1, int position2, int position3, int position4) {
        this.itemSlot1 = itemSlot1;
        this.itemSlot2 = itemSlot2;
        this.itemSlot3 = itemSlot3;
        this.itemSlot4 = itemSlot4;
        this.plateItemSlot = plateItemSlot;

        this.position1 = position1;
        this.position2 = position2;
        this.position3 = position3;
        this.position4 = position4;

        this.itemSlots = Map.of(
                this.position1, this.itemSlot1,
                this.position2, this.itemSlot2,
                this.position3, this.itemSlot3,
                this.position4, this.itemSlot4
        );
    }

    @Override
    public void interact(Player player, InteractionHand hand, ItemStack itemStack, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container) {
        if (itemStack.isEmpty() && player.isCrouching() && container.canBeRemoved()) {
            onRemove(container, pos);
            return;
        }

        if (itemStack.isEmpty()) {
            pos.dropItemStack(getContent(player, hand, position, layer, pos, container, false));
            return;
        }

        if (itemStack.is(HotpotModEntry.HOTPOT_LARGE_ROUND_PLATE)) {
            plateItemSlot.addItem(itemStack);
            return;
        }

        itemSlots.get(position).addItem(itemStack);
    }

    @Override
    public void onRemove(IHotpotPlacementContainer container, LevelBlockPos pos) {
        plateItemSlot.dropItem(pos);
        itemSlot1.dropItem(pos);
        itemSlot2.dropItem(pos);
        itemSlot3.dropItem(pos);
        itemSlot4.dropItem(pos);
    }

    @Override
    public ItemStack getContent(Player player, InteractionHand hand, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container, boolean tableware) {
        return (isEmpty() ? plateItemSlot : itemSlots.get(position)).takeItem(container.canConsumeContents());
    }

    @Override
    public boolean shouldRemove(Player player, InteractionHand hand, ItemStack itemStack, int position, int layer, LevelBlockPos pos, IHotpotPlacementContainer container) {
        return plateItemSlot.isEmpty() && container.canBeRemoved();
    }

    @Override
    public ItemStack getCloneItemStack(IHotpotPlacementContainer container, LevelBlockPos pos) {
        return plateItemSlot.getItemStack();
    }

    @Override
    public List<Integer> getPositions() {
        return List.of(position1, position2, position3, position4);
    }

    @Override
    public Holder<IHotpotPlacementSerializer<?>> getPlacementSerializerHolder() {
        return HotpotPlacementSerializers.LARGE_ROUND_PLATE_SERIALIZER;
    }

    @Override
    public void setCommonItemSlot(ItemStack itemStack) {
        plateItemSlot.set(itemStack);
    }

    public boolean isEmpty() {
        return itemSlot1.isEmpty() && itemSlot2.isEmpty() && itemSlot3.isEmpty() && itemSlot4.isEmpty();
    }

    public SimpleItemSlot getItemSlot1() {
        return itemSlot1;
    }

    public SimpleItemSlot getItemSlot2() {
        return itemSlot2;
    }

    public SimpleItemSlot getItemSlot3() {
        return itemSlot3;
    }

    public SimpleItemSlot getItemSlot4() {
        return itemSlot4;
    }

    public SimpleItemSlot getPlateItemSlot() {
        return plateItemSlot;
    }

    public Map<Integer, SimpleItemSlot> getItemSlots() {
        return itemSlots;
    }

    public int getPosition1() {
        return position1;
    }

    public int getPosition2() {
        return position2;
    }

    public int getPosition3() {
        return position3;
    }

    public int getPosition4() {
        return position4;
    }

    public static class Serializer implements IHotpotPlacementSerializer<HotpotLargeRoundPlate> {
        public static final MapCodec<HotpotLargeRoundPlate> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(plate -> plate.group(
                        SimpleItemSlot.CODEC.fieldOf("item_slot_1").forGetter(HotpotLargeRoundPlate::getItemSlot1),
                        SimpleItemSlot.CODEC.fieldOf("item_slot_2").forGetter(HotpotLargeRoundPlate::getItemSlot2),
                        SimpleItemSlot.CODEC.fieldOf("item_slot_3").forGetter(HotpotLargeRoundPlate::getItemSlot3),
                        SimpleItemSlot.CODEC.fieldOf("item_slot_4").forGetter(HotpotLargeRoundPlate::getItemSlot4),
                        SimpleItemSlot.CODEC.fieldOf("plate_item_slot").forGetter(HotpotLargeRoundPlate::getPlateItemSlot),
                        Codec.INT.fieldOf("position_1").forGetter(HotpotLargeRoundPlate::getPosition1),
                        Codec.INT.fieldOf("position_2").forGetter(HotpotLargeRoundPlate::getPosition2),
                        Codec.INT.fieldOf("position_3").forGetter(HotpotLargeRoundPlate::getPosition3),
                        Codec.INT.fieldOf("position_4").forGetter(HotpotLargeRoundPlate::getPosition4)
                ).apply(plate, HotpotLargeRoundPlate::new))
        );

        @Override
        public HotpotLargeRoundPlate createPlacement(List<Integer> positions, ComplexDirection direction) {
            return new HotpotLargeRoundPlate(positions.getFirst(), positions.get(1), positions.get(2), positions.get(3));
        }

        @Override
        public MapCodec<HotpotLargeRoundPlate> getCodec() {
            return CODEC;
        }

        @Override
        public List<Optional<Integer>> getPositions(int position, ComplexDirection direction) {
            return List.of(Optional.of(position), direction.getClockWiseQuarter().relativeTo(position), direction.getClockWise().relativeTo(position), direction.relativeTo(position));
        }
    }
}
