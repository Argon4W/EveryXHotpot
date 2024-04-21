package com.github.argon4w.hotpot.recipes;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SimpleRecipeAssembler {
    private final CraftingContainer craftingContainer;
    private ItemStack assembled = ItemStack.EMPTY;
    private Predicate<ItemStack> filter = itemStack -> true;

    public SimpleRecipeAssembler(CraftingContainer craftingContainer) {
        this.craftingContainer = craftingContainer;
    }

    public SimpleRecipeAssembler filter(Predicate<ItemStack> predicate) {
        filter = predicate;
        return this;
    }

    public SimpleRecipeAssembler feed(BiConsumer<ItemStack, ItemStack> consumer) {
        for (int i = 0; i < craftingContainer.getContainerSize(); i ++) {
            ItemStack itemStack = craftingContainer.getItem(i);

            if (itemStack.isEmpty()) {
                continue;
            }

            if (!filter.test(itemStack)) {
                continue;
            }

            consumer.accept(assembled, itemStack);
        }

        return this;
    }

    public SimpleRecipeAssembler with(Predicate<ItemStack> predicate) {
        for (int i = 0; i < craftingContainer.getContainerSize(); i ++) {
            ItemStack itemStack = craftingContainer.getItem(i);

            if (itemStack.isEmpty()) {
                continue;
            }

            if (!predicate.test(itemStack)) {
                continue;
            }

            assembled = itemStack.copyWithCount(1);
            return filter(itemStack1 -> !predicate.test(itemStack1));
        }

        return this;
    }

    public SimpleRecipeAssembler with(Supplier<ItemStack> supplier) {
        assembled = supplier.get();
        return this;
    }

    public ItemStack assemble() {
        return assembled;
    }
}
