package com.github.argon4w.hotpot.soups.synchronizers;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;

import java.util.Optional;
import java.util.function.BiConsumer;

public interface IHotpotSoupSynchronizer {
    void collect(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    void integrate(int size, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
}
