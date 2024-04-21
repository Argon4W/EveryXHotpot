package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class HotpotPlacedPaperBowl implements IHotpotPlacement {
    private int pos;
    private int directionSlot;
    private Direction direction;
    private SimpleItemSlot paperBowlItemSlot = new SimpleItemSlot();

    @Override
    public IHotpotPlacement load(CompoundTag compoundTag) {
        pos = compoundTag.getByte("Pos");
        directionSlot = compoundTag.getByte("DirectionPos");
        direction = HotpotPlacements.POS_TO_DIRECTION.get(directionSlot - pos);

        paperBowlItemSlot.load(compoundTag.getCompound("PaperBowl"));

        return this;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putByte("Pos", (byte) pos);
        compoundTag.putByte("DirectionPos", (byte) directionSlot);

        compoundTag.put("PaperBowl", paperBowlItemSlot.save(new CompoundTag()));

        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundTag compoundTag) {
        return compoundTag.contains("Pos", Tag.TAG_BYTE) && compoundTag.contains("DirectionPos", Tag.TAG_BYTE) && compoundTag.contains("PaperBowl", Tag.TAG_COMPOUND);
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(HotpotModEntry.MODID, "placed_paper_bowl");
    }

    @Override
    public boolean interact(Player player, InteractionHand hand, ItemStack itemStack, int pos, HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos selfPos) {
        if (itemStack.isEmpty()) {
            if (player.isCrouching()) {
                return true;
            }

            hotpotPlacementBlockEntity.tryTakeOutContentViaHand(pos, selfPos);

            return paperBowlItemSlot.isEmpty();
        }

        paperBowlItemSlot.addItem(itemStack);

        return false;
    }

    @Override
    public ItemStack takeOutContent(int pos, HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos selfPos) {
        boolean consume = !hotpotPlacementBlockEntity.isInfiniteContent();
        return paperBowlItemSlot.takeItem(consume);
    }

    @Override
    public void onRemove(HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos pos) {

    }

    @Override
    public ItemStack getCloneItemStack(HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos level) {
        return paperBowlItemSlot.getItemStack();
    }

    @Override
    public boolean canPlace(int pos, Direction direction) {
        this.pos = pos;
        this.directionSlot = pos + HotpotPlacements.DIRECTION_TO_POS.get(direction);
        this.direction = direction;

        return true;
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
        this.paperBowlItemSlot = new SimpleItemSlot(paperBowlItemSlot);
    }

    public SimpleItemSlot getPaperBowlItemSlot() {
        return paperBowlItemSlot;
    }
}
