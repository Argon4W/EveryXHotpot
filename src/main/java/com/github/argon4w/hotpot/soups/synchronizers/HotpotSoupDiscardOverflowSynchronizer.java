package com.github.argon4w.hotpot.soups.synchronizers;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;

public class HotpotSoupDiscardOverflowSynchronizer implements IHotpotSoupSynchronizer {
    @Override
    public void collect(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        hotpotBlockEntity.getSoup().discardOverflowWaterLevel(hotpotBlockEntity, pos);
    }

    @Override
    public void integrate(int size, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {

    }
}
