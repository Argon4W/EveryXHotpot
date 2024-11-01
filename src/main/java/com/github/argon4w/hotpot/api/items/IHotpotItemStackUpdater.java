package com.github.argon4w.hotpot.api.items;

import java.util.function.Consumer;
import net.minecraft.world.item.ItemStack;

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
