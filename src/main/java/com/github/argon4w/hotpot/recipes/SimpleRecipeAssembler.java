package com.github.argon4w.hotpot.recipes;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import net.minecraft.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;

public class SimpleRecipeAssembler {
    private final List<ItemStack> items;
    private Predicate<ItemStack> filter;
    private ItemStack assembled;

    public SimpleRecipeAssembler(List<ItemStack> items) {
        this.items = items;
        this.assembled = ItemStack.EMPTY;
        this.filter = itemStack -> true;
    }

    public SimpleRecipeAssembler(CraftingInput input) {
        this(input.items());
    }

    public ItemStack assemble() {
        return assembled;
    }

    public SimpleRecipeAssembler filter(Predicate<ItemStack> predicate) {
        filter = filter.and(predicate);
        return this;
    }

    public ItemStack assemble(UnaryOperator<ItemStack> function) {
        return function.apply(assembled);
    }

    public SimpleRecipeAssembler feed(BiFunction<ItemStack, ItemStack, ItemStack> function) {
        items.stream()
                .filter(Predicate.not(ItemStack::isEmpty))
                .filter(filter)
                .forEach(itemStack -> function.apply(assembled, itemStack));
        return this;
    }

    public SimpleRecipeAssembler basedOn(Predicate<ItemStack> predicate) {
        return items.stream()
                .filter(Predicate.not(ItemStack::isEmpty))
                .filter(predicate)
                .findFirst()
                .map(itemStack -> Util.make(
                        filter(Predicate.not(predicate)),
                        assembler -> assembler.assembled = itemStack.copyWithCount(1)))
                .orElse(this);
    }
}
