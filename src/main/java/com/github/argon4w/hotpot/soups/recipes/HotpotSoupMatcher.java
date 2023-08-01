package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotCampfireRecipeContent;
import com.github.argon4w.hotpot.contents.HotpotEmptyContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class HotpotSoupMatcher {
    private final HotpotBlockEntity hotpotBlockEntity;
    private boolean matched = true;

    public HotpotSoupMatcher(HotpotBlockEntity hotpotBlockEntity) {
        this.hotpotBlockEntity = hotpotBlockEntity;
    }

    public HotpotSoupMatcher withSoup(Predicate<IHotpotSoup> predicate) {
        matched = matched && predicate.test(hotpotBlockEntity.getSoup());

        return this;
    }

    public HotpotSoupMatchContext withItem(Predicate<ItemStack> predicate) {
        return with(content -> content instanceof HotpotCampfireRecipeContent itemStackContent && predicate.test(itemStackContent.getItemStack()));
    }

    public HotpotSoupMatchContext with(Predicate<IHotpotContent> predicate) {
        return new HotpotSoupMatchContext(this, predicate);
    }

    public HotpotSoupMatcher range(int from, int to, Predicate<IHotpotContent> predicate) {
        if (!matched) {
            return this;
        }

        int count = (int) hotpotBlockEntity.getContents().stream().filter(content -> !(content instanceof HotpotEmptyContent) && predicate.test(content)).count();
        matched = matched && (count >= from && count <= to);

        return this;
    }

    public boolean match() {
        return matched;
    }

    public static record HotpotSoupMatchContext(HotpotSoupMatcher matcher, Predicate<IHotpotContent> predicate) {
        public HotpotSoupMatcher all() {
            return require(matcher.hotpotBlockEntity.getContents().size());
        }

        public HotpotSoupMatcher atLeast(int count) {
            return matcher.range(count, Integer.MAX_VALUE, predicate);
        }

        public HotpotSoupMatcher require(int count) {
            return matcher.range(count, count, predicate);
        }
    }
}
