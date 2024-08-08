package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.SimpleItemSlot;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class HotpotLongPlate implements IHotpotPlate {
    private final int pos1;
    private final int pos2;
    private final Direction direction;

    private final SimpleItemSlot itemSlot1;
    private final SimpleItemSlot itemSlot2;
    private final SimpleItemSlot plateItemSlot;

    public HotpotLongPlate(int pos, Direction direction) {
        this.pos1 = pos;
        this.pos2 = pos + HotpotPlacementSerializers.DIRECTION_TO_POS.get(direction);
        this.direction = direction;
        this.itemSlot1 = new SimpleItemSlot();
        this.itemSlot2 = new SimpleItemSlot();
        this.plateItemSlot = new SimpleItemSlot();
    }

    public HotpotLongPlate(int pos1, int pos2, SimpleItemSlot itemSlot1, SimpleItemSlot itemSlot2, SimpleItemSlot plateItemSlot) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.direction = HotpotPlacementSerializers.POS_TO_DIRECTION.get(pos2 - pos1);

        this.itemSlot1 = itemSlot1;
        this.itemSlot2 = itemSlot2;
        this.plateItemSlot = plateItemSlot;
    }

    @Override
    public boolean interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container) {
        if (itemStack.isEmpty() && player.isCrouching()) {
            return true;
        }

        if (itemStack.isEmpty()) {
            selfPos.dropItemStack(takeOutContent(pos, layer, selfPos, container, true));
            return plateItemSlot.isEmpty();
        }

        if (itemStack.is(HotpotModEntry.HOTPOT_LONG_PLATE)) {
            plateItemSlot.addItem(itemStack);
            return plateItemSlot.isEmpty();
        }

        SimpleItemSlot preferred = pos == pos1 ? itemSlot1 : itemSlot2;
        SimpleItemSlot fallback = pos == pos1 ? itemSlot2 : itemSlot1;

        if (fallback.isSame(itemStack) && fallback.addItem(itemStack)) {
            return plateItemSlot.isEmpty();
        }

        if (!preferred.addItem(itemStack)) {
            fallback.addItem(itemStack);
        }

        return plateItemSlot.isEmpty();
    }

    @Override
    public ItemStack takeOutContent(int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container, boolean tableware) {
        boolean consume = !container.isInfiniteContent();

        if (pos == pos1 && !itemSlot1.isEmpty()) {
            return itemSlot1.takeItem(consume);
        }

        if (!itemSlot2.isEmpty()) {
            return itemSlot2.takeItem(consume);
        }

        return plateItemSlot.takeItem(consume);
    }

    @Override
    public void onRemove(IHotpotPlacementContainerBlockEntity container, LevelBlockPos pos) {
        itemSlot1.dropItem(pos);
        itemSlot2.dropItem(pos);
    }

    @Override
    public ItemStack getCloneItemStack(IHotpotPlacementContainerBlockEntity container, LevelBlockPos selfPos) {
        return plateItemSlot.getItemStack();
    }

    @Override
    public List<Integer> getPoslist() {
        return List.of(pos1, pos2);
    }

    @Override
    public boolean isConflict(int pos) {
        return pos1 == pos || pos2 == pos;
    }

    @Override
    public Holder<IHotpotPlacementSerializer<?>> getPlacementSerializerHolder() {
        return HotpotPlacementSerializers.LONG_PLATE_SERIALIZER;
    }

    @Override
    public void setPlateItemSlot(ItemStack itemStack) {
        plateItemSlot.set(itemStack);
    }

    public int getPos1() {
        return pos1;
    }

    public int getPos2() {
        return pos2;
    }

    public Direction getDirection() {
        return direction;
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
                        Codec.INT.fieldOf("pos_1").forGetter(HotpotLongPlate::getPos1),
                        Codec.INT.fieldOf("pos_2").forGetter(HotpotLongPlate::getPos2),
                        SimpleItemSlot.CODEC.fieldOf("item_slot_1").forGetter(HotpotLongPlate::getItemSlot1),
                        SimpleItemSlot.CODEC.fieldOf("item_slot_2").forGetter(HotpotLongPlate::getItemSlot2),
                        SimpleItemSlot.CODEC.fieldOf("plate_item_slot").forGetter(HotpotLongPlate::getPlateItemSlot)
                ).apply(plate, HotpotLongPlate::new))
        );

        @Override
        public HotpotLongPlate get(int pos, Direction direction) {
            return new HotpotLongPlate(pos, direction);
        }

        @Override
        public MapCodec<HotpotLongPlate> getCodec() {
            return CODEC;
        }

        @Override
        public boolean canPlace(int pos, Direction direction) {
            return isValidPos(pos, pos + HotpotPlacementSerializers.DIRECTION_TO_POS.get(direction));
        }

        public boolean isValidPos(int pos1, int pos2) {
            return 0 <= pos1 && pos1 <= 3 && 0 <= pos2 && pos2 <= 3 && pos1 + pos2 != 3;
        }
    }
}
