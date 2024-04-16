package com.github.argon4w.hotpot.spices;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SimpleRecipeMatcher {
    private final List<ItemStack> items;
    private boolean matched = true;

    public SimpleRecipeMatcher(CraftingContainer craftingContainer) {
        this(craftingContainer.getItems());
    }

    public SimpleRecipeMatcher(List<ItemStack> list) {
        this.items = new ArrayList<>(list);
    }

    public SimpleRecipeMatcher collect(Predicate<ItemStack> predicate, Consumer<ItemStack> consumer) {
        items.stream().filter(predicate).forEach(consumer);
        return this;
    }

    public SimpleRecipeMatcher discard(List<ItemStack> list) {
        items.removeAll(list);
        return this;
    }

    public SimpleRecipeMatchContext with(Predicate<ItemStack> predicate) {
        return new SimpleRecipeMatchContext(this, predicate);
    }

    public SimpleRecipeMatchContext withRemaining() {
        return new SimpleRecipeMatchContext(this, itemStack -> true);
    }

    public SimpleRecipeMatchContext withEmpty() {
        return new SimpleRecipeMatchContext(this, ItemStack::isEmpty);
    }

    public SimpleRecipeMatcher mismatch() {
        this.matched = false;

        return this;
    }

    public boolean match() {
        return items.size() == 0 && matched;
    }

    public static class SimpleRecipeMatchContext {
        private final SimpleRecipeMatcher matcher;
        private final List<ItemStack> collected;

        public SimpleRecipeMatchContext(SimpleRecipeMatcher matcher, Predicate<ItemStack> predicate) {
            this.matcher = matcher;
            this.collected = new ArrayList<>();

            matcher.collect(predicate, collected::add);
        }

        public SimpleRecipeMatchContext collect(Consumer<ItemStack> consumer) {
            collected.forEach(consumer);

            return this;
        }

        public SimpleRecipeMatcher once() {
            return require(1);
        }

        public SimpleRecipeMatcher require(int count) {
            return range(count, count);
        }

        public SimpleRecipeMatcher atLeast(int from) {
            return range(from, Integer.MAX_VALUE);
        }

        public SimpleRecipeMatcher range(int from, int to) {
            return (collected.size() >= from && collected.size() <= to) ? matcher.discard(collected) : matcher.mismatch();
        }

        public SimpleRecipeMatcher discard() {
            return matcher.discard(collected);
        }

        public SimpleRecipeMatcher empty() {
            return collected.stream().allMatch(ItemStack::isEmpty) ? matcher.discard(collected) : matcher.mismatch();
        }
    }
}
