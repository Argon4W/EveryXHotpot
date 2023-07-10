package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;

@FunctionalInterface
public interface IHotpotSoupPredicate {
    boolean test(HotpotBlockEntity blockEntity, BlockPosWithLevel pos);
}
