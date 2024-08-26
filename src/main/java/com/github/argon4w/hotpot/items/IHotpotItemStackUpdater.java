package com.github.argon4w.hotpot.items;

import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

@FunctionalInterface
public interface IHotpotItemStackUpdater {
    ItemStack update(ItemStack itemStack, Consumer<ItemStack> consumer);

    static IHotpotItemStackUpdater pass() {
        return (itemStack, consumer) -> {
            consumer.accept(itemStack);
            return itemStack;
        };
    }
}
