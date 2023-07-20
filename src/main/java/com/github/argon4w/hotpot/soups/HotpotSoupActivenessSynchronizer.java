package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;

public class HotpotSoupActivenessSynchronizer implements IHotpotSoupSynchronizer {
    private float collectedActiveness = 0f;

    @Override
    public void collect(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        IHotpotSoup soup = hotpotBlockEntity.getSoup();

        if (soup instanceof IHotpotSoupWithActiveness withActiveness) {
            collectedActiveness += withActiveness.getActiveness(hotpotBlockEntity, pos);
        }
    }

    @Override
    public void integrate(int size, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        float averageActiveness = Math.max(0f, Math.min(1f, collectedActiveness / size));

        if (hotpotBlockEntity.getSoup() instanceof IHotpotSoupWithActiveness withActiveness) {
            withActiveness.setActiveness(hotpotBlockEntity, pos, averageActiveness);
        }
    }
}
