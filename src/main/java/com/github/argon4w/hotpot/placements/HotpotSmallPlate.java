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

public class HotpotSmallPlate implements IHotpotPlacement {
    private final int pos;
    private final int directionPos;
    private final Direction direction;
    private final SimpleItemSlot itemSlot;

    public HotpotSmallPlate(int pos, Direction direction) {
        this.pos = pos;
        this.direction = direction;
        this.itemSlot = new SimpleItemSlot();
        this.directionPos = pos + HotpotPlacements.DIRECTION_TO_POS.get(direction);
    }

    public HotpotSmallPlate(int pos, int directionPos, SimpleItemSlot itemSlot) {
        this.pos = pos;
        this.directionPos = directionPos;
        this.itemSlot = itemSlot;
        this.direction = HotpotPlacements.POS_TO_DIRECTION.get(directionPos - pos);
    }

    @Override
    public boolean interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos selfPos) {
        if (!itemStack.isEmpty()) {
            itemSlot.addItem(itemStack);
            return false;
        }

        if (player.isCrouching()) {
            return true;
        }

        hotpotPlateBlockEntity.tryTakeOutContentViaHand(pos, selfPos);

        return false;
    }

    @Override
    public ItemStack takeOutContent(int pos, HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos selfPos, boolean tableware) {
        return itemSlot.takeItem(!hotpotPlateBlockEntity.isInfiniteContent());
    }

    @Override
    public void onRemove(HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos pos) {
        itemSlot.dropItem(pos);
    }

    @Override
    public ItemStack getCloneItemStack(HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos level) {
        return new ItemStack(HotpotModEntry.HOTPOT_SMALL_PLATE_BLOCK_ITEM.get());
    }

    @Override
    public List<Integer> getPoslist() {
        return List.of(pos);
    }

    @Override
    public boolean isConflict(int pos) {
        return this.pos == pos;
    }

    @Override
    public Holder<IHotpotPlacementFactory<?>> getPlacementFactoryHolder() {
        return HotpotPlacements.SMALL_PLATE;
    }

    public int getPos() {
        return pos;
    }

    public int getDirectionPos() {
        return directionPos;
    }

    public Direction getDirection() {
        return direction;
    }

    public SimpleItemSlot getItemSlot() {
        return itemSlot;
    }

    public static class Factory implements IHotpotPlacementFactory<HotpotSmallPlate> {
        public static final MapCodec<HotpotSmallPlate> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(plate -> plate.group(
                        Codec.INT.fieldOf("Pos").forGetter(HotpotSmallPlate::getPos),
                        Codec.INT.fieldOf("DirectionPos").forGetter(HotpotSmallPlate::getDirectionPos),
                        SimpleItemSlot.CODEC.fieldOf("ItemSlot").forGetter(HotpotSmallPlate::getItemSlot)
                ).apply(plate, HotpotSmallPlate::new))
        );

        @Override
        public HotpotSmallPlate buildFromSlots(int pos, Direction direction) {
            return new HotpotSmallPlate(pos, direction);
        }

        @Override
        public MapCodec<HotpotSmallPlate> buildFromCodec() {
            return CODEC;
        }

        @Override
        public boolean canPlace(int pos, Direction direction) {
            return true;
        }
    }
}
