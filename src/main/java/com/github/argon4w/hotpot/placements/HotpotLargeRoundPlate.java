package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.SimpleItemSlot;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class HotpotLargeRoundPlate implements IHotpotPlate {
    private final SimpleItemSlot itemSlot1;
    private final SimpleItemSlot itemSlot2;
    private final SimpleItemSlot itemSlot3;
    private final SimpleItemSlot itemSlot4;
    private final SimpleItemSlot plateItemSlot;
    private final SimpleItemSlot[] slots;

    public HotpotLargeRoundPlate() {
        this.itemSlot1 = new SimpleItemSlot();
        this.itemSlot2 = new SimpleItemSlot();
        this.itemSlot3 = new SimpleItemSlot();
        this.itemSlot4 = new SimpleItemSlot();
        this.plateItemSlot = new SimpleItemSlot();
        slots = new SimpleItemSlot[] {itemSlot1, itemSlot2, itemSlot3, itemSlot4};
    }

    public HotpotLargeRoundPlate(SimpleItemSlot itemSlot1, SimpleItemSlot itemSlot2, SimpleItemSlot itemSlot3, SimpleItemSlot itemSlot4, SimpleItemSlot plateItemSlot) {
        this.itemSlot1 = itemSlot1;
        this.itemSlot2 = itemSlot2;
        this.itemSlot3 = itemSlot3;
        this.itemSlot4 = itemSlot4;
        this.plateItemSlot = plateItemSlot;
        slots = new SimpleItemSlot[] {itemSlot1, itemSlot2, itemSlot3, itemSlot4};
    }

    @Override
    public boolean interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container) {
        if (itemStack.isEmpty() && player.isCrouching()) {
            return true;
        }

        if (itemStack.isEmpty()) {
            selfPos.dropItemStack(takeOutContent(pos, layer, selfPos, container, false));
            return plateItemSlot.isEmpty();
        }

        if (itemStack.is(HotpotModEntry.HOTPOT_LARGE_ROUND_PLATE)) {
            plateItemSlot.addItem(itemStack);
            return plateItemSlot.isEmpty();
        }

        slots[pos].addItem(itemStack);
        return plateItemSlot.isEmpty();
    }

    @Override
    public ItemStack takeOutContent(int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container, boolean tableware) {
        boolean consume = !container.isInfiniteContent();
        return isEmpty() ? plateItemSlot.takeItem(consume) : slots[pos].takeItem(consume);
    }

    @Override
    public void onRemove(IHotpotPlacementContainerBlockEntity container, LevelBlockPos pos) {
        itemSlot1.dropItem(pos);
        itemSlot2.dropItem(pos);
        itemSlot3.dropItem(pos);
        itemSlot4.dropItem(pos);
    }

    @Override
    public ItemStack getCloneItemStack(IHotpotPlacementContainerBlockEntity container, LevelBlockPos selfPos) {
        return plateItemSlot.getItemStack();
    }

    @Override
    public List<Integer> getPoslist() {
        return List.of(0, 1, 2, 3);
    }

    @Override
    public boolean isConflict(int pos) {
        return true;
    }

    @Override
    public void setPlateItemSlot(ItemStack itemStack) {
        plateItemSlot.set(itemStack);
    }

    public boolean isEmpty() {
        return slots[0].isEmpty() && slots[1].isEmpty() && slots[2].isEmpty() && slots[3].isEmpty();
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

    public SimpleItemSlot[] getSlots() {
        return slots;
    }

    @Override
    public Holder<IHotpotPlacementSerializer<?>> getPlacementSerializerHolder() {
        return HotpotPlacementSerializers.LARGE_ROUND_PLATE_SERIALIZER;
    }

    public static class Serializer implements IHotpotPlacementSerializer<HotpotLargeRoundPlate> {
        public static final MapCodec<HotpotLargeRoundPlate> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(plate -> plate.group(
                        SimpleItemSlot.CODEC.fieldOf("item_slot_1").forGetter(HotpotLargeRoundPlate::getItemSlot1),
                        SimpleItemSlot.CODEC.fieldOf("item_slot_2").forGetter(HotpotLargeRoundPlate::getItemSlot2),
                        SimpleItemSlot.CODEC.fieldOf("item_slot_3").forGetter(HotpotLargeRoundPlate::getItemSlot3),
                        SimpleItemSlot.CODEC.fieldOf("item_slot_4").forGetter(HotpotLargeRoundPlate::getItemSlot4),
                        SimpleItemSlot.CODEC.fieldOf("plate_item_slot").forGetter(HotpotLargeRoundPlate::getPlateItemSlot)
                ).apply(plate, HotpotLargeRoundPlate::new))
        );

        @Override
        public HotpotLargeRoundPlate get(int pos, Direction direction) {
            return new HotpotLargeRoundPlate();
        }

        @Override
        public MapCodec<HotpotLargeRoundPlate> getCodec() {
            return CODEC;
        }

        @Override
        public boolean canPlace(int pos, Direction direction) {
            return true;
        }
    }
}
