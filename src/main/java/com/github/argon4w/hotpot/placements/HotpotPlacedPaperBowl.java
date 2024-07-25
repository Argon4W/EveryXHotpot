package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.items.HotpotPaperBowlItem;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HotpotPlacedPaperBowl implements IHotpotPlacement {
    private final int pos;
    private final int directionPos;
    private final Direction direction;
    private final SimpleItemSlot paperBowlItemSlot = new SimpleItemSlot();

    public HotpotPlacedPaperBowl(int pos, Direction direction) {
        this.pos = pos;
        this.directionPos = pos + HotpotPlacements.DIRECTION_TO_POS.get(direction);
        this.direction = direction;
    }

    public HotpotPlacedPaperBowl(int pos, int directionPos, CompoundTag itemSlotTag, HolderLookup.Provider registryAccess) {
        this.pos = pos;
        this.directionPos = directionPos;
        this.direction = HotpotPlacements.POS_TO_DIRECTION.get(directionPos - pos);
        this.paperBowlItemSlot.load(itemSlotTag, registryAccess);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
        compoundTag.putByte("Pos", (byte) pos);
        compoundTag.putByte("DirectionPos", (byte) directionPos);

        compoundTag.put("PaperBowl", paperBowlItemSlot.save(new CompoundTag(), registryAccess));

        return compoundTag;
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "placed_paper_bowl");
    }

    @Override
    public boolean interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos selfPos) {
        if (!itemStack.isEmpty()) {
            paperBowlItemSlot.addItem(itemStack);
            return false;
        }

        if (player.isCrouching()) {
            return true;
        }

        hotpotPlacementBlockEntity.tryTakeOutContentViaHand(pos, selfPos);

        return false;
    }

    @Override
    public ItemStack takeOutContent(int pos, HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos selfPos, boolean tableware) {
        boolean consume = !hotpotPlacementBlockEntity.isInfiniteContent();
        ItemStack paperBowl = paperBowlItemSlot.getItemStack();

        if (HotpotPaperBowlItem.isBowlClear(paperBowl)) {
            return ItemStack.EMPTY;
        }

        if (HotpotPaperBowlItem.isBowlEmpty(paperBowl)) {
            removePaperBowl(hotpotPlacementBlockEntity, pos, selfPos);
            return ItemStack.EMPTY;
        }

        List<ItemStack> itemStacks;

        if (tableware) {
            itemStacks = new ArrayList<>(HotpotPaperBowlItem.getPaperBowlItems(paperBowl));
        } else {
            itemStacks = new ArrayList<>(HotpotPaperBowlItem.getPaperBowlSkewers(paperBowl));
        }

        if (itemStacks.size() == 0) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStack = itemStacks.get(0);

        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (consume) {
            itemStacks.remove(0);
        }

        if (tableware) {
            HotpotPaperBowlItem.setPaperBowlItems(paperBowl, itemStacks);
        } else {
            HotpotPaperBowlItem.setPaperBowlSkewers(paperBowl, itemStacks);
        }

        if (HotpotPaperBowlItem.isBowlEmpty(paperBowl)) {
            removePaperBowl(hotpotPlacementBlockEntity, pos, selfPos);
        }

        return itemStack;
    }

    private void removePaperBowl(HotpotPlacementBlockEntity hotpotPlacementBlockEntity, int pos, LevelBlockPos selfPos) {
        paperBowlItemSlot.clear();
        hotpotPlacementBlockEntity.tryRemove(pos, selfPos);
    }

    @Override
    public void onRemove(HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos pos) {

    }

    @Override
    public ItemStack getCloneItemStack(HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos level) {
        return paperBowlItemSlot.getItemStack();
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

    public void setPaperBowlItemSlot(ItemStack paperBowlItemSlot) {
        this.paperBowlItemSlot.set(paperBowlItemSlot);
    }

    public SimpleItemSlot getPaperBowlItemSlot() {
        return paperBowlItemSlot;
    }

    public static class Factory implements IHotpotPlacementFactory<HotpotPlacedPaperBowl> {

        @Override
        public HotpotPlacedPaperBowl buildFromSlots(int pos, Direction direction, HolderLookup.Provider registryAccess) {
            return new HotpotPlacedPaperBowl(pos, direction);
        }

        @Override
        public HotpotPlacedPaperBowl buildFromTag(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
            return new HotpotPlacedPaperBowl(compoundTag.getByte("Pos"), compoundTag.getByte("DirectionPos"), compoundTag.getCompound("PaperBowl"), registryAccess);
        }

        @Override
        public boolean isValid(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
            return compoundTag.contains("Pos", Tag.TAG_BYTE) && compoundTag.contains("DirectionPos", Tag.TAG_BYTE) && compoundTag.contains("PaperBowl", Tag.TAG_COMPOUND);
        }

        @Override
        public boolean canPlace(int pos, Direction direction) {
            return true;
        }
    }
}
