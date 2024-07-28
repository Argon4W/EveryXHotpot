package com.github.argon4w.hotpot.soups.synchronizers;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;

public interface IHotpotSoupSynchronizer {
    void collect(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    void integrate(int size, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
}
