package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;

public class HotpotSoupWaterLevelSynchronizer implements IHotpotSoupSynchronizer {
    private float collectedWaterLevel = 0f;

    @Override
    public void collect(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        IHotpotSoup soup = hotpotBlockEntity.getSoup();
        collectedWaterLevel += soup.getWaterLevel(hotpotBlockEntity, pos) + soup.getOverflowWaterLevel(hotpotBlockEntity, pos);
    }

    @Override
    public void integrate(int size, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        float averageWaterLevel = Math.max(0f, Math.min(1f, collectedWaterLevel / size));

        hotpotBlockEntity.getSoup().setWaterLevel(hotpotBlockEntity, pos, averageWaterLevel);
    }
}
