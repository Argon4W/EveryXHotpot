package com.github.argon4w.hotpot.soups.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotCampfireRecipeContent;
import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.contents.HotpotEmptyContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.HotpotSoups;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class HotpotSoupFactory {
    private final HotpotBlockEntity hotpotBlockEntity;
    private HashMap<Predicate<IHotpotContent>, UnaryOperator<IHotpotContent>> queuedReplaces;
    private MatchStatus matched = MatchStatus.DEFAULT;

    public HotpotSoupFactory(HotpotBlockEntity hotpotBlockEntity) {
        this.hotpotBlockEntity = hotpotBlockEntity;
        this.queuedReplaces = new HashMap<>();
    }

    protected Optional<IHotpotSoup> assemble(String key) {
        queuedReplaces.forEach((predicate, operator) -> hotpotBlockEntity.consumeContent(content -> predicate.test(content) ? operator.apply(content) : content));
        return Optional.of(HotpotSoups.getSoupRegistry().getValue(new ResourceLocation(HotpotModEntry.MODID, key)).createSoup());
    }

    protected int range(int from, int to, Predicate<IHotpotContent> predicate, boolean minimal) {
        int count = (int) hotpotBlockEntity.getContents().stream().filter(content -> !(content instanceof HotpotEmptyContent) && predicate.test(content)).count();
        matched = locked() ? MatchStatus.LOCKED : ((matched() && (count >= from && count <= to)) ? MatchStatus.MATCH : MatchStatus.MISMATCH);

        return minimal ? from : count;
    }

    protected boolean matched() {
        return matched != MatchStatus.MISMATCH;
    }

    protected boolean defaulted() {
        return matched == MatchStatus.DEFAULT;
    }

    protected boolean locked() {
        return matched == MatchStatus.LOCKED;
    }

    public Optional<IHotpotSoup> match(String key) {
        return matched() ? assemble(key) : Optional.empty();
    }

    public HotpotSoupFactory withSoup(Predicate<IHotpotSoup> predicate) {
        matched = locked() ? MatchStatus.LOCKED : ((matched() && predicate.test(hotpotBlockEntity.getSoup())) ? MatchStatus.MATCH : MatchStatus.MISMATCH);
        return this;
    }

    public HotpotSoupFactoryMatchContext withItem(Predicate<ItemStack> predicate) {
        return with(content -> content instanceof HotpotCampfireRecipeContent itemStackContent && predicate.test(itemStackContent.getItemStack()));
    }

    public HotpotSoupFactoryMatchContext with(Predicate<IHotpotContent> predicate) {
        return new HotpotSoupFactoryMatchContext(this, predicate);
    }

    public HotpotSoupFactory withVariant(Supplier<Boolean> condition) {
        queuedReplaces = matched() && !defaulted() ? queuedReplaces : new HashMap<>();
        matched = matched() && !defaulted() ? MatchStatus.LOCKED : (condition.get() ? MatchStatus.DEFAULT : MatchStatus.MISMATCH);

        return this;
    }

    public HotpotSoupFactory withDefault() {
        return withVariant(() -> true);
    }

    public record HotpotSoupFactoryMatchContext(HotpotSoupFactory factory, Predicate<IHotpotContent> predicate) {
        public HotpotSoupFactoryAssembleContext all() {
            return require(factory.hotpotBlockEntity.getContents().size());
        }

        public HotpotSoupFactoryAssembleContext atLeast(int count, boolean minimal) {
            return new HotpotSoupFactoryAssembleContext(factory, predicate, factory.range(count, Integer.MAX_VALUE, predicate, minimal));
        }

        public HotpotSoupFactoryAssembleContext atLeast(int count) {
            return new HotpotSoupFactoryAssembleContext(factory, predicate, factory.range(count, Integer.MAX_VALUE, predicate, true));
        }

        public HotpotSoupFactoryAssembleContext require(int count) {
            return new HotpotSoupFactoryAssembleContext(factory, predicate, factory.range(count, count, predicate, false));
        }
    }

    public record HotpotSoupFactoryAssembleContext(HotpotSoupFactory factory, Predicate<IHotpotContent> predicate, int amount) {
        public HotpotSoupFactory consume() {
            return replace(content -> HotpotContents.getEmptyContent().createContent());
        }

        public HotpotSoupFactory replace(UnaryOperator<IHotpotContent> operator) {
            return factory.locked() ? factory : enqueueOperator(operator);
        }

        private HotpotSoupFactory enqueueOperator(UnaryOperator<IHotpotContent> operator) {
            AtomicInteger count = new AtomicInteger(0);
            factory.queuedReplaces.put(predicate, content -> count.getAndIncrement() < amount ? operator.apply(content) : content);

            return factory;
        }
    }

    public enum MatchStatus {
        DEFAULT, MATCH, MISMATCH, LOCKED
    }
}
