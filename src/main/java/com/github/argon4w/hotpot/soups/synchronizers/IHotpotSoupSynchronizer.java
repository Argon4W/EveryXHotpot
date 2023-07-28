package com.github.argon4w.hotpot.soups.synchronizers;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;

import java.util.Optional;
import java.util.function.BiConsumer;

public interface IHotpotSoupSynchronizer {
    void collect(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    void integrate(int size, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);

    default IHotpotSoupSynchronizer andThen(IHotpotSoupSynchronizer after) {
        return combine(this, after);
    }

    default IHotpotSoupSynchronizer andThen(BiConsumer<HotpotBlockEntity, BlockPosWithLevel> consumer) {
        return combine(this, collectOnly(consumer));
    }

    default Optional<IHotpotSoupSynchronizer> ofOptional() {
        return Optional.of(this);
    }

    static IHotpotSoupSynchronizer empty() {
        return new IHotpotSoupSynchronizer() {
            @Override
            public void collect(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {

            }

            @Override
            public void integrate(int size, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {

            }
        };
    }

    static IHotpotSoupSynchronizer combine(IHotpotSoupSynchronizer before, IHotpotSoupSynchronizer after) {
        return new IHotpotSoupSynchronizer() {
            @Override
            public void collect(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
                before.collect(hotpotBlockEntity, pos);
                after.collect(hotpotBlockEntity, pos);
            }

            @Override
            public void integrate(int size, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
                before.integrate(size, hotpotBlockEntity, pos);
                after.integrate(size, hotpotBlockEntity, pos);
            }
        };
    }

    static IHotpotSoupSynchronizer collectOnly(BiConsumer<HotpotBlockEntity, BlockPosWithLevel> consumer) {
        return new IHotpotSoupSynchronizer() {
            @Override
            public void collect(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
                consumer.accept(hotpotBlockEntity, pos);
            }

            @Override
            public void integrate(int size, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {

            }
        };
    }
}
