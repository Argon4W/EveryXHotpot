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

public class HotpotPlacedChopstick implements IHotpotPlacement {
    private final int pos1;
    private final int pos2;
    private final Direction direction;
    private final SimpleItemSlot chopstickItemSlot = new SimpleItemSlot();

    public HotpotPlacedChopstick(int pos, Direction direction) {
        this.pos1 = pos;
        this.pos2 = pos + HotpotPlacements.DIRECTION_TO_POS.get(direction);
        this.direction = direction;
    }

    public HotpotPlacedChopstick(int pos1, int pos2, CompoundTag itemSlotTag, HolderLookup.Provider registryAccess) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.direction = HotpotPlacements.POS_TO_DIRECTION.get(pos2 - pos1);
        this.chopstickItemSlot.load(itemSlotTag, registryAccess);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
        compoundTag.putByte("Pos1", (byte) pos1);
        compoundTag.putByte("Pos2", (byte) pos2);
        compoundTag.put("Chopstick", chopstickItemSlot.save(new CompoundTag(), registryAccess));

        return compoundTag;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "placed_chopstick");
    }

    @Override
    public boolean interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos selfPos) {
        return true;
    }

    @Override
    public ItemStack takeOutContent(int pos, HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos selfPos, boolean tableware) {
        return ItemStack.EMPTY;
    }

    @Override
    public void onRemove(HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos pos) {

    }

    @Override
    public ItemStack getCloneItemStack(HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos level) {
        return chopstickItemSlot.getItemStack();
    }

    @Override
    public List<Integer> getPos() {
        return List.of(pos1, pos2);
    }

    @Override
    public boolean isConflict(int pos) {
        return pos1 == pos || pos2 == pos;
    }

    public void setChopstickItemSlot(ItemStack chopstickItemSlot) {
        this.chopstickItemSlot.set(chopstickItemSlot);
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

    public SimpleItemSlot getChopstickItemSlot() {
        return chopstickItemSlot;
    }

    public static class Factory implements IHotpotPlacementFactory<HotpotPlacedChopstick> {

        @Override
        public HotpotPlacedChopstick buildFromSlots(int pos, Direction direction, HolderLookup.Provider registryAccess) {
            return new HotpotPlacedChopstick(pos, direction);
        }

        @Override
        public HotpotPlacedChopstick buildFromTag(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
            return new HotpotPlacedChopstick(compoundTag.getByte("Pos1"), compoundTag.getByte("Pos2"), compoundTag.getCompound("Chopstick"), registryAccess);
        }

        @Override
        public boolean isValid(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
            return compoundTag.contains("Pos1", Tag.TAG_BYTE) && compoundTag.contains("Pos2", Tag.TAG_BYTE) && compoundTag.contains("Chopstick", Tag.TAG_COMPOUND);
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
