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

public class HotpotSmallPlate implements IHotpotPlate {
    private final int pos;
    private final int directionPos;
    private final Direction direction;
    private final SimpleItemSlot itemSlot;
    private final SimpleItemSlot plateItemSlot;

    public HotpotSmallPlate(int pos, Direction direction) {
        this.pos = pos;
        this.direction = direction;
        this.itemSlot = new SimpleItemSlot();
        this.plateItemSlot = new SimpleItemSlot();
        this.directionPos = pos + HotpotPlacementSerializers.DIRECTION_TO_POS.get(direction);
    }

    public HotpotSmallPlate(int pos, int directionPos, SimpleItemSlot itemSlot, SimpleItemSlot plateItemSlot) {
        this.pos = pos;
        this.directionPos = directionPos;
        this.itemSlot = itemSlot;
        this.plateItemSlot = plateItemSlot;
        this.direction = HotpotPlacementSerializers.POS_TO_DIRECTION.get(directionPos - pos);
    }

    @Override
    public boolean interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container) {
        if (!itemStack.isEmpty()) {
            (itemStack.is(HotpotModEntry.HOTPOT_SMALL_PLATE) ? plateItemSlot : itemSlot).addItem(itemStack);
            return plateItemSlot.isEmpty();
        }

        if (player.isCrouching()) {
            return true;
        }

        selfPos.dropItemStack(takeOutContent(pos, layer, selfPos, container, false));
        return plateItemSlot.isEmpty();
    }

    @Override
    public ItemStack takeOutContent(int pos, int layer, LevelBlockPos selfPos, IHotpotPlacementContainerBlockEntity container, boolean tableware) {
        return itemSlot.isEmpty() ? plateItemSlot.takeItem(!container.isInfiniteContent()) : itemSlot.takeItem(!container.isInfiniteContent());
    }

    @Override
    public void onRemove(IHotpotPlacementContainerBlockEntity container, LevelBlockPos pos) {
        itemSlot.dropItem(pos);
    }

    @Override
    public ItemStack getCloneItemStack(IHotpotPlacementContainerBlockEntity container, LevelBlockPos selfPos) {
        return plateItemSlot.getItemStack();
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
    public Holder<IHotpotPlacementSerializer<?>> getPlacementSerializerHolder() {
        return HotpotPlacementSerializers.SMALL_PLATE_SERIALIZER;
    }

    @Override
    public void setPlateItemSlot(ItemStack itemStack) {
        plateItemSlot.set(itemStack);
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

    public SimpleItemSlot getPlateItemSlot() {
        return plateItemSlot;
    }

    public static class Serializer implements IHotpotPlacementSerializer<HotpotSmallPlate> {
        public static final MapCodec<HotpotSmallPlate> CODEC = LazyMapCodec.of(() ->
                RecordCodecBuilder.mapCodec(plate -> plate.group(
                        Codec.INT.fieldOf("Pos").forGetter(HotpotSmallPlate::getPos),
                        Codec.INT.fieldOf("DirectionPos").forGetter(HotpotSmallPlate::getDirectionPos),
                        SimpleItemSlot.CODEC.fieldOf("ItemSlot").forGetter(HotpotSmallPlate::getItemSlot),
                        SimpleItemSlot.CODEC.fieldOf("PlateItemSlot").forGetter(HotpotSmallPlate::getPlateItemSlot)
                ).apply(plate, HotpotSmallPlate::new))
        );

        @Override
        public HotpotSmallPlate get(int pos, Direction direction) {
            return new HotpotSmallPlate(pos, direction);
        }

        @Override
        public MapCodec<HotpotSmallPlate> getCodec() {
            return CODEC;
        }

        @Override
        public boolean canPlace(int pos, Direction direction) {
            return true;
        }
    }
}
