package com.github.argon4w.hotpot.api.items;

import net.minecraft.world.item.ItemStack;

public interface IHotpotItemContainer {
    ItemStack getContainedItemStack(ItemStack itemStack);
}
