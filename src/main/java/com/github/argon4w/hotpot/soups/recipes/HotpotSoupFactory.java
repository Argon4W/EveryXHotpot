package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotCampfireRecipeContent;
import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.contents.HotpotEmptyContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.HotpotSoups;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class HotpotSoupFactory {
    private final HotpotBlockEntity hotpotBlockEntity;
    private final HashMap<Predicate<IHotpotContent>, UnaryOperator<IHotpotContent>> queuedReplaces;
    private boolean matched = true;

    public HotpotSoupFactory(HotpotBlockEntity hotpotBlockEntity) {
        this.hotpotBlockEntity = hotpotBlockEntity;
        this.queuedReplaces = new HashMap<>();
    }

    protected Optional<IHotpotSoup> assemble(String key) {
        queuedReplaces.forEach((predicate, operator) -> {
            //hotpotBlockEntity.consumeContent(content -> predicate.test(content) ? operator.apply(content) : content)
            System.out.println("1");
        });
        return Optional.of(HotpotSoups.getSoupOrElseEmpty(key).get());
    }

    protected int range(int from, int to, Predicate<IHotpotContent> predicate) {
        int count = (int) hotpotBlockEntity.getContents().stream().filter(content -> !(content instanceof HotpotEmptyContent) && predicate.test(content)).count();
        matched = matched && (count >= from && count <= to);

        return count;
    }

    public Optional<IHotpotSoup> match(String key) {
        System.out.println(matched);
        return matched ? assemble(key) : Optional.empty();
    }

    public HotpotSoupFactory withSoup(Predicate<IHotpotSoup> predicate) {
        matched = matched && predicate.test(hotpotBlockEntity.getSoup());
        return this;
    }

    public HotpotSoupFactoryMatchContext withItem(Predicate<ItemStack> predicate) {
        return with(content -> content instanceof HotpotCampfireRecipeContent itemStackContent && predicate.test(itemStackContent.getItemStack()));
    }

    public HotpotSoupFactoryMatchContext with(Predicate<IHotpotContent> predicate) {
        return new HotpotSoupFactoryMatchContext(this, predicate);
    }

    public record HotpotSoupFactoryMatchContext(HotpotSoupFactory factory, Predicate<IHotpotContent> predicate) {
        public HotpotSoupFactoryAssembleContext all() {
            return require(factory.hotpotBlockEntity.getContents().size());
        }

        public HotpotSoupFactoryAssembleContext atLeast(int count) {
            return new HotpotSoupFactoryAssembleContext(factory, predicate, factory.range(count, Integer.MAX_VALUE, predicate));
        }

        public HotpotSoupFactoryAssembleContext require(int count) {
            return new HotpotSoupFactoryAssembleContext(factory, predicate, factory.range(count, count, predicate));
        }
    }

    public record HotpotSoupFactoryAssembleContext(HotpotSoupFactory factory, Predicate<IHotpotContent> predicate, int amount) {
        public HotpotSoupFactory consume() {
            return replace(content -> HotpotContents.getEmptyContent().get());
        }

        public HotpotSoupFactory replace(UnaryOperator<IHotpotContent> operator) {
            AtomicInteger count = new AtomicInteger(0);
            factory.queuedReplaces.put(predicate, content -> count.getAndIncrement() < amount ? operator.apply(content) : content);

            return factory;
        }
    }
}
