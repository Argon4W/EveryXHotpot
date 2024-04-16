package com.github.argon4w.hotpot.soups.synchronizers;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.github.argon4w.hotpot.soups.IHotpotSoupTypeWithActiveness;

public class HotpotSoupActivenessSynchronizer implements IHotpotSoupSynchronizer {
    private float collectedActiveness = 0f;

    @Override
    public void collect(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        IHotpotSoupType soup = hotpotBlockEntity.getSoup();

        if (soup instanceof IHotpotSoupTypeWithActiveness withActiveness) {
            collectedActiveness += withActiveness.getActiveness(hotpotBlockEntity, pos);
        }
    }

    @Override
    public void integrate(int size, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        float averageActiveness = Math.max(0f, Math.min(1f, collectedActiveness / size));

        if (hotpotBlockEntity.getSoup() instanceof IHotpotSoupTypeWithActiveness withActiveness) {
            withActiveness.setActiveness(hotpotBlockEntity, pos, averageActiveness);
        }
    }
}
