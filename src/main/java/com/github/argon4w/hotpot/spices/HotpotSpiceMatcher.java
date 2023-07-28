package com.github.argon4w.hotpot.spices;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class HotpotSpiceMatcher {
    private final List<ItemStack> items;
    private boolean matched = true;

    public HotpotSpiceMatcher(CraftingContainer craftingContainer) {
        this(craftingContainer.getItems());
    }

    public HotpotSpiceMatcher(List<ItemStack> list) {
        this.items = new ArrayList<>(list);
    }

    public HotpotSpiceMatcher collect(Predicate<ItemStack> predicate, Consumer<ItemStack> consumer) {
        items.stream().filter(predicate).forEach(consumer);
        return this;
    }

    public HotpotSpiceMatcher discard(List<ItemStack> list) {
        items.removeAll(list);
        return this;
    }

    public HotpotSpiceMatchContext with(Predicate<ItemStack> predicate) {
        return new HotpotSpiceMatchContext(this, predicate);
    }

    public HotpotSpiceMatchContext withRemaining() {
        return new HotpotSpiceMatchContext(this, itemStack -> true);
    }

    public HotpotSpiceMatchContext withEmpty() {
        return new HotpotSpiceMatchContext(this, ItemStack::isEmpty);
    }

    public HotpotSpiceMatcher mismatch() {
        this.matched = false;

        return this;
    }

    public boolean match() {
        System.out.println(items);
        return items.size() == 0 && matched;
    }

    public static class HotpotSpiceMatchContext {
        private final HotpotSpiceMatcher matcher;
        private final List<ItemStack> collected;

        public HotpotSpiceMatchContext(HotpotSpiceMatcher matcher, Predicate<ItemStack> predicate) {
            this.matcher = matcher;
            this.collected = new ArrayList<>();

            matcher.collect(predicate, collected::add);
        }

        public HotpotSpiceMatchContext collect(Consumer<ItemStack> consumer) {
            collected.forEach(consumer);

            return this;
        }

        public HotpotSpiceMatcher once() {
            return require(1);
        }

        public HotpotSpiceMatcher require(int count) {
            return range(count, count);
        }

        public HotpotSpiceMatcher atLeast(int from) {
            return range(from, Integer.MAX_VALUE);
        }

        public HotpotSpiceMatcher range(int from, int to) {
            return (collected.size() >= from && collected.size() <= to) ? matcher.discard(collected) : matcher.mismatch();
        }

        public HotpotSpiceMatcher discard() {
            return matcher.discard(collected);
        }

        public HotpotSpiceMatcher empty() {
            return collected.stream().allMatch(ItemStack::isEmpty) ? matcher.discard(collected) : matcher.mismatch();
        }
    }
}
