package com.github.argon4w.hotpot.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class SimpleRecipeAssembler {
    private final CraftingInput input;

    private ItemStack assembled;
    private Predicate<ItemStack> filter;

    public SimpleRecipeAssembler(CraftingInput input) {
        this.input = input;
        this.assembled = ItemStack.EMPTY;
        this.filter = itemStack -> true;
    }

    public SimpleRecipeAssembler filter(Predicate<ItemStack> predicate) {
        filter = filter.and(predicate);
        return this;
    }

    public SimpleRecipeAssembler feed(BiFunction<ItemStack, ItemStack, ItemStack> function) {
        for (int i = 0; i < input.size(); i ++) {
            ItemStack itemStack = input.getItem(i);

            if (itemStack.isEmpty()) {
                continue;
            }

            if (!filter.test(itemStack)) {
                continue;
            }

            assembled = function.apply(assembled, itemStack);
        }

        return this;
    }

    public SimpleRecipeAssembler with(Predicate<ItemStack> predicate) {
        for (int i = 0; i < input.size(); i ++) {
            ItemStack itemStack = input.getItem(i);

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

    public ItemStack assemble(UnaryOperator<ItemStack> function) {
        return function.apply(assembled);
    }
}
