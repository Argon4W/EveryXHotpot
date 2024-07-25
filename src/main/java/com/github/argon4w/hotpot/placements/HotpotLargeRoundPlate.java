package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
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

public class HotpotLargeRoundPlate implements IHotpotPlacement {
    private final SimpleItemSlot itemSlot1 = new SimpleItemSlot();
    private final SimpleItemSlot itemSlot2 = new SimpleItemSlot();
    private final SimpleItemSlot itemSlot3 = new SimpleItemSlot();
    private final SimpleItemSlot itemSlot4 = new SimpleItemSlot();
    private final SimpleItemSlot[] slots = {itemSlot1, itemSlot2, itemSlot3, itemSlot4};

    public HotpotLargeRoundPlate() {

    }

    public HotpotLargeRoundPlate(CompoundTag itemSlotTag1, CompoundTag itemSlotTag2, CompoundTag itemSlotTag3, CompoundTag itemSlotTag4, HolderLookup.Provider registryAccess) {
        itemSlot1.load(itemSlotTag1, registryAccess);
        itemSlot2.load(itemSlotTag2, registryAccess);
        itemSlot3.load(itemSlotTag3, registryAccess);
        itemSlot4.load(itemSlotTag4, registryAccess);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
        compoundTag.put("ItemSlot1", itemSlot1.save(new CompoundTag(), registryAccess));
        compoundTag.put("ItemSlot2", itemSlot2.save(new CompoundTag(), registryAccess));
        compoundTag.put("ItemSlot3", itemSlot3.save(new CompoundTag(), registryAccess));
        compoundTag.put("ItemSlot4", itemSlot4.save(new CompoundTag(), registryAccess));

        return compoundTag;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "large_round_plate");
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

        slots[pos].addItem(itemStack);
        return false;
    }

    @Override
    public ItemStack takeOutContent(int pos, HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos selfPos, boolean tableware) {
        return slots[pos].takeItem(!hotpotPlateBlockEntity.isInfiniteContent());
    }

    @Override
    public void onRemove(HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos pos) {
        itemSlot1.dropItem(pos);
        itemSlot2.dropItem(pos);
        itemSlot3.dropItem(pos);
        itemSlot4.dropItem(pos);
    }

    @Override
    public ItemStack getCloneItemStack(HotpotPlacementBlockEntity hotpotPlateBlockEntity, LevelBlockPos level) {
        return new ItemStack(HotpotModEntry.HOTPOT_LARGE_ROUND_PLATE_BLOCK_ITEM.get());
    }

    @Override
    public List<Integer> getPos() {
        return List.of(0, 1, 2, 3);
    }

    @Override
    public boolean isConflict(int pos) {
        return true;
    }

    public SimpleItemSlot[] getSlots() {
        return slots;
    }

    public static class Factory implements IHotpotPlacementFactory<HotpotLargeRoundPlate> {
        @Override
        public HotpotLargeRoundPlate buildFromSlots(int pos, Direction direction, HolderLookup.Provider registryAccess) {
            return new HotpotLargeRoundPlate();
        }

        @Override
        public HotpotLargeRoundPlate buildFromTag(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
            return new HotpotLargeRoundPlate(compoundTag.getCompound("ItemSLot1"), compoundTag.getCompound("ItemSlot2"), compoundTag.getCompound("ItemSlot3"), compoundTag.getCompound("ItemSlot4"), registryAccess);
        }

        @Override
        public boolean isValid(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
            return compoundTag.contains("ItemSlot1", Tag.TAG_COMPOUND) && compoundTag.contains("ItemSlot2", Tag.TAG_COMPOUND) && compoundTag.contains("ItemSlot4", Tag.TAG_COMPOUND) && compoundTag.contains("ItemSlot4", Tag.TAG_COMPOUND);
        }

        @Override
        public boolean canPlace(int pos, Direction direction) {
            return true;
        }
    }
}
