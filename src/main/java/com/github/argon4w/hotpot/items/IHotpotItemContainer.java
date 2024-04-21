package com.github.argon4w.hotpot.items;

import net.minecraft.world.item.ItemStack;

public interface IHotpotItemContainer {
    ItemStack getContainedItemStack(ItemStack itemStack);
}
