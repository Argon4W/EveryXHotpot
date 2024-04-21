package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.LevelBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class SimpleItemSlot {
    private ItemStack itemSlot;

    public SimpleItemSlot(ItemStack itemStack) {
        this.itemSlot = itemStack;
    }

    public SimpleItemSlot() {
        this(ItemStack.EMPTY);
    }

    public int getStackCount() {
        if (itemSlot.isEmpty()) {
            return 0;
        }

        if (itemSlot.getMaxStackSize() < 4) {
            return itemSlot.getCount();
        }

        return Math.max(1, Math.round(itemSlot.getCount() / (itemSlot.getMaxStackSize() / 4f)));
    }

    public boolean addItem(ItemStack itemStack) {
        if (itemSlot.isEmpty()) {
            itemSlot = itemStack.copyAndClear();

            return true;
        }

        if (ItemStack.isSameItemSameTags(itemStack, itemSlot)) {
            moveItemWithCount(itemStack);

            return itemStack.isEmpty();
        }

        return false;
    }

    private void moveItemWithCount(ItemStack itemStack) {
        int j = Math.min(itemStack.getCount(), itemSlot.getMaxStackSize() - itemSlot.getCount());
        if (j > 0) {
            itemSlot.grow(j);
            itemStack.shrink(j);
        }
    }

    public ItemStack takeItem(boolean consume) {
        return consume ?  itemSlot.split(1) : itemSlot.copyWithCount(1);
    }

    public boolean isEmpty() {
        return itemSlot.isEmpty();
    }

    public void dropItem(LevelBlockPos pos) {
        pos.dropItemStack(itemSlot.copyAndClear());
    }

    public CompoundTag save(CompoundTag compoundTag) {
        itemSlot.save(compoundTag);

        return compoundTag;
    }

    public void load(CompoundTag compoundTag) {
        itemSlot = ItemStack.of(compoundTag);
    }

    public ItemStack getItemStack() {
        return itemSlot;
    }
}
