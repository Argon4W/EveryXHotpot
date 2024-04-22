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

public class HotpotPlacedSpoon implements IHotpotPlacement {
    private int pos1, pos2;
    private ItemStack spoonItemStack = ItemStack.EMPTY;
    private Direction direction;

    @Override
    public IHotpotPlacement load(CompoundTag compoundTag) {
        pos1 = compoundTag.getByte("Pos1");
        pos2 = compoundTag.getByte("Pos2");
        direction = HotpotPlacements.POS_TO_DIRECTION.get(pos2 - pos1);

        spoonItemStack = ItemStack.of(compoundTag.getCompound("Spoon"));

        return this;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putByte("Pos1", (byte) pos1);
        compoundTag.putByte("Pos2", (byte) pos2);

        compoundTag.put("Spoon", spoonItemStack.save(new CompoundTag()));

        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundTag compoundTag) {
        return compoundTag.contains("Pos1", Tag.TAG_BYTE) && compoundTag.contains("Pos2", Tag.TAG_BYTE) && compoundTag.contains("Spoon", Tag.TAG_COMPOUND);
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(HotpotModEntry.MODID, "placed_spoon");
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
        return spoonItemStack;
    }

    @Override
    public boolean canPlace(int pos, Direction direction) {
        int pos2 = pos + HotpotPlacements.DIRECTION_TO_POS.get(direction);
        if (isValidPos(pos, pos2)) {
            this.pos1 = pos;
            this.pos2 = pos2;
            this.direction = direction;

            return true;
        }

        return false;
    }

    public boolean isValidPos(int pos1, int pos2) {
        return 0 <= pos1 && pos1 <= 3 && 0 <= pos2 && pos2 <= 3 && pos1 + pos2 != 3;
    }

    @Override
    public List<Integer> getPos() {
        return List.of(pos1, pos2);
    }

    @Override
    public boolean isConflict(int pos) {
        return pos1 == pos || pos2 == pos;
    }

    public void setSpoonItemStack(ItemStack spoonItemStack) {
        this.spoonItemStack = spoonItemStack;
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

    public ItemStack getSpoonItemStack() {
        return spoonItemStack;
    }
}
