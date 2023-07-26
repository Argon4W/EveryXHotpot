package com.github.argon4w.hotpot.spice;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class CraftingMatcher {
    private final CraftingContainer craftingContainer;
    private boolean matched = true;

    public CraftingMatcher(CraftingContainer craftingContainer) {
        this.craftingContainer = craftingContainer;
    }

    public CraftingMatcher atLeast(int count, Predicate<ItemStack> predicate) {
        return range(count, Integer.MAX_VALUE, predicate);
    }

    public CraftingMatcher require(int count, Predicate<ItemStack> predicate) {
        return range(count, count, predicate);
    }

    public CraftingMatcher range(int from, int to, Predicate<ItemStack> predicate) {
        if (!matched) {
            return this;
        }

        int count = 0;

        for (int i = 0; i < craftingContainer.getContainerSize(); i ++) {
            ItemStack itemStack = craftingContainer.getItem(i);

            if (!itemStack.isEmpty() && predicate.test(itemStack)) {
                count ++;
            }
        }

        matched = matched && (count >= from && count <= to);

        return this;
    }

    public boolean match() {
        return matched;
    }
}
