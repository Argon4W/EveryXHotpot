package com.github.argon4w.hotpot.soups.synchronizers;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.github.argon4w.hotpot.soups.IHotpotSoupTypeWithActiveness;

public class HotpotSoupTickSynchronizer implements IHotpotSoupSynchronizer {
    @Override
    public void collect(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        hotpotBlockEntity.getSoup().tick(hotpotBlockEntity, pos);
    }

    @Override
    public void integrate(int size, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {

    }
}
