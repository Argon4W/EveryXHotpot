package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LazyMapCodec;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class HotpotLongPlate implements IHotpotPlacement {
    private final int pos1;
    private final int pos2;
    private final Direction direction;

    private final SimpleItemSlot itemSlot1;
    private final SimpleItemSlot itemSlot2;

    public HotpotLongPlate(int pos, Direction direction) {
        this.pos1 = pos;
        this.pos2 = pos + HotpotPlacements.DIRECTION_TO_POS.get(direction);
        this.direction = direction;
        this.itemSlot1 = new SimpleItemSlot();
        this.itemSlot2 = new SimpleItemSlot();
    }

    public HotpotLongPlate(int pos1, int pos2, SimpleItemSlot itemSlot1, SimpleItemSlot itemSlot2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.direction = HotpotPlacements.POS_TO_DIRECTION.get(pos2 - pos1);

        this.itemSlot1 = itemSlot1;
        this.itemSlot2 = itemSlot2;
    }

    @Override
    public boolean interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos selfPos) {
        if (itemStack.isEmpty() && player.isCrouching()) {
            return true;
        }

        if (itemStack.isEmpty()) {
            hotpotPlacementBlockEntity.tryTakeOutContentViaHand(pos, selfPos);
            return false;
        }

        SimpleItemSlot preferred = pos == pos1 ? itemSlot1 : itemSlot2;
        SimpleItemSlot fallback = pos == pos1 ? itemSlot2 : itemSlot1;

        if (fallback.isSame(itemStack) && fallback.addItem(itemStack)) {
            return false;
        }

        if (!preferred.addItem(itemStack)) {
            fallback.addItem(itemStack);
        }

        return false;
    }

    @Override
    public ItemStack takeOutContent(int pos, HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos selfPos, boolean tableware) {
        boolean consume = !hotpotPlacementBlockEntity.isInfiniteContent();
        return pos == pos1 ? (itemSlot1.isEmpty() ? itemSlot2.takeItem(consume) : itemSlot1.takeItem(consume)) : itemSlot2.takeItem(consume);
    }

    @Override
    public void onRemove(HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos pos) {
        itemSlot1.dropItem(pos);
        itemSlot2.dropItem(pos);
    }

    @Override
    public ItemStack getCloneItemStack(HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos level) {
        return new ItemStack(HotpotModEntry.HOTPOT_LONG_PLATE_BLOCK_ITEM.get());
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
    public Holder<IHotpotPlacementFactory<?>> getPlacementFactoryHolder() {
        return HotpotPlacements.LONG_PLATE;
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

    public static class Factory implements IHotpotPlacementFactory<HotpotLongPlate> {
        public static final MapCodec<HotpotLongPlate> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(plate -> plate.group(
                        Codec.INT.fieldOf("Pos1").forGetter(HotpotLongPlate::getPos1),
                        Codec.INT.fieldOf("Pos2").forGetter(HotpotLongPlate::getPos2),
                        SimpleItemSlot.CODEC.fieldOf("ItemSlot1").forGetter(HotpotLongPlate::getItemSlot1),
                        SimpleItemSlot.CODEC.fieldOf("ItemSlot1").forGetter(HotpotLongPlate::getItemSlot2)
                ).apply(plate, HotpotLongPlate::new))
        );

        @Override
        public HotpotLongPlate buildFromSlots(int pos, Direction direction) {
            return new HotpotLongPlate(pos, direction);
        }

        @Override
        public MapCodec<HotpotLongPlate> buildFromCodec() {
            return CODEC;
        }

        @Override
        public boolean canPlace(int pos, Direction direction) {
            return isValidPos(pos, pos + HotpotPlacements.DIRECTION_TO_POS.get(direction));
        }

        public boolean isValidPos(int pos1, int pos2) {
            return 0 <= pos1 && pos1 <= 3 && 0 <= pos2 && pos2 <= 3 && pos1 + pos2 != 3;
        }
    }
}
