package com.github.argon4w.hotpot.spice;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CraftingAssembler {
    private final CraftingContainer craftingContainer;
    private ItemStack assembled = ItemStack.EMPTY;
    private Predicate<ItemStack> filter = itemStack -> true;

    public CraftingAssembler(CraftingContainer craftingContainer) {
        this.craftingContainer = craftingContainer;
    }

    public CraftingAssembler filter(Predicate<ItemStack> predicate) {
        filter = predicate;

        return this;
    }

    public CraftingAssembler forEach(BiConsumer<ItemStack, ItemStack> consumer) {
        for (int i = 0; i < craftingContainer.getContainerSize(); i ++) {
            ItemStack itemStack = craftingContainer.getItem(i);

            if (!itemStack.isEmpty() && filter.test(itemStack)) {
                consumer.accept(assembled, itemStack);
            }
        }

        return this;
    }

    public CraftingAssembler withExisting(Predicate<ItemStack> predicate, Supplier<ItemStack> supplier) {
        for (int i = 0; i < craftingContainer.getContainerSize(); i ++) {
            ItemStack itemStack = craftingContainer.getItem(i);

            if (!itemStack.isEmpty() && predicate.test(itemStack)) {
                assembled = itemStack.copyWithCount(1);

                return filter(itemStack1 -> !predicate.test(itemStack1));
            }
        }

        return with(supplier);
    }

    public CraftingAssembler with(Supplier<ItemStack> supplier) {
        assembled = supplier.get();

        return this;
    }

    public ItemStack assemble() {
        return assembled;
    }
}
