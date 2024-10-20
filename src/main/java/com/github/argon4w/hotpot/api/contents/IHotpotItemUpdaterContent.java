package com.github.argon4w.hotpot.api.contents;

import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public interface IHotpotItemUpdaterContent extends IHotpotContent {
    void updateItemStack(Consumer<ItemStack> consumer);
}
