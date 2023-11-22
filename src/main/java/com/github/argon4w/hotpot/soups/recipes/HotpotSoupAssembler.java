package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotCampfireRecipeContent;
import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.HotpotSoups;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class HotpotSoupAssembler {
    private final HotpotBlockEntity hotpotBlockEntity;

    public HotpotSoupAssembler(HotpotBlockEntity hotpotBlockEntity) {
        this.hotpotBlockEntity = hotpotBlockEntity;
    }

    public HotpotSoupAssembler operate(int atMost, Predicate<IHotpotContent> predicate, UnaryOperator<IHotpotContent> operator) {
        AtomicInteger count = new AtomicInteger();
        hotpotBlockEntity.consumeContent(content -> predicate.test(content) && count.getAndIncrement() < atMost ? operator.apply(content) : content);

        return this;
    }

    public HotpotSoupAssembleContext withItem(Predicate<ItemStack> predicate) {
        return with(content -> content instanceof HotpotCampfireRecipeContent itemStackContent && predicate.test(itemStackContent.getItemStack()));
    }

    public HotpotSoupAssembleContext with(Predicate<IHotpotContent> predicate) {
        return new HotpotSoupAssembleContext(this, predicate);
    }

    public IHotpotSoup assemble(String key) {
        return HotpotSoups.getSoupOrElseEmpty(key).get();
    }

    public static record HotpotSoupAssembleContext(HotpotSoupAssembler assembler, Predicate<IHotpotContent> predicate) {
        public HotpotSoupAssembler consume() {
            return consume(Integer.MAX_VALUE);
        }

        public HotpotSoupAssembler consume(int stMost) {
            return replace(content -> HotpotContents.getEmptyContent().get(), stMost);
        }

        public HotpotSoupAssembler replace(UnaryOperator<IHotpotContent> operator) {
            return replace(operator, Integer.MAX_VALUE);
        }

        public HotpotSoupAssembler replace(UnaryOperator<IHotpotContent> operator, int atMost) {
            return assembler.operate(atMost, predicate, operator);
        }
    }
}
