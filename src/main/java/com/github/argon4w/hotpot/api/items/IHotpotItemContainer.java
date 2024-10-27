package com.github.argon4w.hotpot.api.items;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IHotpotItemContainer {
    ItemStack getContainedItemStack(ItemStack itemStack);
    List<ItemStack> getAllContainedItemStacks(ItemStack itemStack);
}
