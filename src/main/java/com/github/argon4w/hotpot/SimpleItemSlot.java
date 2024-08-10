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

    public SimpleItemSlot transferItem(ItemStack itemStack) {
        this.itemStack = transfer(this.itemStack, itemStack);
        return this;
    }

    public boolean addItem(ItemStack itemStack) {
        transferItem(itemStack);
        return itemStack.isEmpty();
    }

    public SimpleItemSlot dropItem(LevelBlockPos pos) {
        pos.dropItemStack(itemStack.copyAndClear());
        return this;
    }

    public SimpleItemSlot shrink(int count) {
        itemStack.shrink(count);
        return this;
    }

    public int getRenderCount() {
        return getRenderCount(4);
    }

    public int getRenderCount(float maxCount) {
        return isEmpty() ? 0 : getRenderCountNotEmpty(maxCount);
    }

    public int getRenderCountNotEmpty(float maxCount) {
        return getMaxStackSize() < maxCount ? getCount() : Math.max(1, Math.round(getCount() / (getMaxStackSize() / maxCount)));
    }

    public int getCount() {
        return itemStack.getCount();
    }

    public int getMaxStackSize() {
        return itemStack.getMaxStackSize();
    }

    public SimpleItemSlot copy() {
        return new SimpleItemSlot(itemStack.copy());
    }

    public ItemStack takeItem(boolean consume) {
        return consume ?  itemStack.split(1) : itemStack.copyWithCount(1);
    }

    public boolean isEmpty() {
        return itemStack.isEmpty();
    }

    public void set(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public void clear() {
        itemStack = ItemStack.EMPTY;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SimpleItemSlot slot && ItemStack.isSameItemSameComponents(itemStack, slot.itemStack) && itemStack.getCount() == slot.itemStack.getCount();
    }

    public static ItemStack transfer(ItemStack from, ItemStack to) {
        if (to.isEmpty()) {
            return from.copyAndClear();
        }

        if (from.isEmpty()) {
            return to;
        }

        if (!ItemStack.isSameItemSameComponents(from, to)) {
            return to;
        }

        int transferCount = Math.clamp(to.getMaxStackSize() - to.getCount(), 0, from.getCount());

        if (transferCount == 0) {
            return to;
        }

        to.grow(transferCount);
        from.shrink(transferCount);

        return to;
    }
}
