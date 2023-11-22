package com.github.argon4w.hotpot.spices;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class HotpotSpiceAssembler {
    private final CraftingContainer craftingContainer;
    private ItemStack assembled = ItemStack.EMPTY;
    private Predicate<ItemStack> filter = itemStack -> true;

    public HotpotSpiceAssembler(CraftingContainer craftingContainer) {
        this.craftingContainer = craftingContainer;
    }

    public HotpotSpiceAssembler filter(Predicate<ItemStack> predicate) {
        filter = predicate;

        return this;
    }

    public HotpotSpiceAssembler forEach(BiConsumer<ItemStack, ItemStack> consumer) {
        for (int i = 0; i < craftingContainer.getContainerSize(); i ++) {
            ItemStack itemStack = craftingContainer.getItem(i);

            if (!itemStack.isEmpty() && filter.test(itemStack)) {
                consumer.accept(assembled, itemStack);
            }
        }

        return this;
    }

    public HotpotSpiceAssembler withExisting(Predicate<ItemStack> predicate, Supplier<ItemStack> supplier) {
        for (int i = 0; i < craftingContainer.getContainerSize(); i ++) {
            ItemStack itemStack = craftingContainer.getItem(i);

            if (!itemStack.isEmpty() && predicate.test(itemStack)) {
                assembled = itemStack.copy();
                assembled.setCount(1);

                return filter(itemStack1 -> !predicate.test(itemStack1));
            }
        }

        return with(supplier);
    }

    public HotpotSpiceAssembler with(Supplier<ItemStack> supplier) {
        assembled = supplier.get();

        return this;
    }

    public ItemStack assemble() {
        return assembled;
    }
}
