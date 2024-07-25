package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class HotpotSmallPlate implements IHotpotPlacement {
    private final int pos;
    private final int directionPos;
    private final Direction direction;
    private final SimpleItemSlot itemSlot = new SimpleItemSlot();

    public HotpotSmallPlate(int pos, Direction direction) {
        this.pos = pos;
        this.directionPos = pos + HotpotPlacements.DIRECTION_TO_POS.get(direction);
        this.direction = direction;
    }

    public HotpotSmallPlate(int pos, int directionPos, CompoundTag itemSlotTag, HolderLookup.Provider registryAccess) {
        this.pos = pos;
        this.directionPos = directionPos;
        this.direction = HotpotPlacements.POS_TO_DIRECTION.get(directionPos - pos);
        this.itemSlot.load(itemSlotTag, registryAccess);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
        compoundTag.putByte("Pos", (byte) pos);
        compoundTag.putByte("DirectionPos", (byte) directionPos);

        compoundTag.put("ItemSlot", itemSlot.save(new CompoundTag(), registryAccess));

        return compoundTag;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "small_plate");
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
    public List<Integer> getPos() {
        return List.of(pos);
    }

    @Override
    public boolean isConflict(int pos) {
        return this.pos == pos;
    }

    public int getPos1() {
        return pos;
    }

    public Direction getDirection() {
        return direction;
    }

    public SimpleItemSlot getItemSlot() {
        return itemSlot;
    }

    public static class Factory implements IHotpotPlacementFactory<HotpotSmallPlate> {

        @Override
        public HotpotSmallPlate buildFromSlots(int pos, Direction direction, HolderLookup.Provider registryAccess) {
            return new HotpotSmallPlate(pos, direction);
        }

        @Override
        public HotpotSmallPlate buildFromTag(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
            return new HotpotSmallPlate(compoundTag.getByte("Pos"), compoundTag.getByte("DirectionPos"), compoundTag.getCompound("ItemSlot"), registryAccess);
        }

        @Override
        public boolean canPlace(int pos, Direction direction) {
            return true;
        }

        @Override
        public boolean isValid(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
            return compoundTag.contains("Pos", Tag.TAG_BYTE) && compoundTag.contains("DirectionPos", Tag.TAG_BYTE) && compoundTag.contains("ItemSlot", Tag.TAG_COMPOUND);
        }
    }
}
