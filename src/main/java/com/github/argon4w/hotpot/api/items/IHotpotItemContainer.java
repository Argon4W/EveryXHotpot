package com.github.argon4w.hotpot.api.items;

import java.util.List;
import net.minecraft.world.item.ItemStack;

public interface IHotpotItemContainer {

    ItemStack getContainedItemStack(ItemStack itemStack);
    List<ItemStack> getAllContainedItemStacks(ItemStack itemStack);
}
