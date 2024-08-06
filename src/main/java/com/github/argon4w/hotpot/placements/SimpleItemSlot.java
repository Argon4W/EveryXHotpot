package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.LevelBlockPos;
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

    private ItemStack itemSlot;

    public SimpleItemSlot(ItemStack itemStack) {
        this.itemSlot = itemStack;
    }

    public SimpleItemSlot() {
        this(ItemStack.EMPTY);
    }

    public int getStackCount() {
        return getStackCount(4);
    }

    public int getStackCount(float maxRenderCount) {
        if (itemSlot.isEmpty()) {
            return 0;
        }

        if (itemSlot.getMaxStackSize() < 4) {
            return itemSlot.getCount();
        }

        return Math.max(1, Math.round(itemSlot.getCount() / (itemSlot.getMaxStackSize() / maxRenderCount)));
    }

    public boolean addItem(ItemStack itemStack) {
        if (itemSlot.isEmpty()) {
            itemSlot = itemStack.copyAndClear();

            return true;
        }

        if (!isSame(itemStack)) {
            return false;
        }

        moveItemWithCount(itemStack);
        return itemStack.isEmpty();
    }

    private void moveItemWithCount(ItemStack itemStack) {
        int j = Math.min(itemStack.getCount(), itemSlot.getMaxStackSize() - itemSlot.getCount());
        if (j > 0) {
            itemSlot.grow(j);
            itemStack.shrink(j);
        }
    }

    public SimpleItemSlot copy() {
        return new SimpleItemSlot(itemSlot.copy());
    }

    public boolean isSame(ItemStack itemStack) {
        return ItemStack.isSameItemSameComponents(itemSlot, itemStack);
    }

    public ItemStack takeItem(boolean consume) {
        return consume ?  itemSlot.split(1) : itemSlot.copyWithCount(1);
    }

    public boolean isEmpty() {
        return itemSlot.isEmpty();
    }

    public void set(ItemStack itemStack) {
        this.itemSlot = itemStack;
    }

    public void clear() {
        itemSlot = ItemStack.EMPTY;
    }

    public void dropItem(LevelBlockPos pos) {
        pos.dropItemStack(itemSlot.copyAndClear());
    }

    public ItemStack getItemStack() {
        return itemSlot;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SimpleItemSlot slot && ItemStack.isSameItemSameComponents(itemSlot, slot.itemSlot);
    }
}
