package com.github.argon4w.hotpot;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public class SimpleItemSlot {
    public static final Codec<SimpleItemSlot> CODEC = Codec.lazyInitialized(() ->
            ItemStack.OPTIONAL_CODEC.xmap(SimpleItemSlot::new, SimpleItemSlot::getItemStack)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SimpleItemSlot> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
            ItemStack.OPTIONAL_STREAM_CODEC.map(SimpleItemSlot::new, SimpleItemSlot::getItemStack)
    );

    private ItemStack itemStack;

    public SimpleItemSlot(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public SimpleItemSlot() {
        this(ItemStack.EMPTY);
    }

    public int getStackCount() {
        return getStackCount(4);
    }

    public int getStackCount(float maxCount) {
        return SimpleItemSlot.getItemStackRenderedCount(itemStack, 4);
    }

    public SimpleItemSlot copy() {
        return new SimpleItemSlot(itemStack.copy());
    }

    public boolean isSame(ItemStack itemStack) {
        return ItemStack.isSameItemSameComponents(this.itemStack, itemStack);
    }

    public ItemStack takeItem(boolean consume) {
        return consume ?  itemStack.split(1) : itemStack.copyWithCount(1);
    }

    public boolean isEmpty() {
        return itemStack.isEmpty();
    }

    public boolean isFull() {
        return itemStack.getMaxStackSize() == itemStack.getCount();
    }

    public void set(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public void clear() {
        itemStack = ItemStack.EMPTY;
    }

    public void dropItem(LevelBlockPos pos) {
        pos.dropItemStack(itemStack.copyAndClear());
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SimpleItemSlot slot && ItemStack.isSameItemSameComponents(itemStack, slot.itemStack);
    }

    private void moveItemWithCount(ItemStack itemStack) {
        int j = Math.min(itemStack.getCount(), this.itemStack.getMaxStackSize() - this.itemStack.getCount());
        if (j > 0) {
            this.itemStack.grow(j);
            itemStack.shrink(j);
        }
    }

    public boolean addItem(ItemStack itemStack) {
        if (this.itemStack.isEmpty()) {
            this.itemStack = itemStack.copyAndClear();

            return true;
        }

        if (!isSame(itemStack)) {
            return false;
        }

        moveItemWithCount(itemStack);
        return itemStack.isEmpty();
    }

    public static int getItemStackRenderedCount(ItemStack itemStack, float maxCount) {
        if (itemStack.isEmpty()) {
            return 0;
        }

        if (itemStack.getMaxStackSize() < maxCount) {
            return itemStack.getCount();
        }

        return Math.max(1, Math.round(itemStack.getCount() / (itemStack.getMaxStackSize() / maxCount)));
    }
}
