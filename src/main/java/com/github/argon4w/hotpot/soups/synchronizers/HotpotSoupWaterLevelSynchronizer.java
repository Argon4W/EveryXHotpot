package com.github.argon4w.hotpot.soups.synchronizers;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.IHotpotSoup;

public class HotpotSoupWaterLevelSynchronizer implements IHotpotSoupSynchronizer {
    private float collectedWaterLevel = 0f;

    @Override
    public void collect(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        IHotpotSoup soup = hotpotBlockEntity.getSoup();
        collectedWaterLevel += soup.getWaterLevel() + soup.getOverflowWaterLevel();
    }

    @Override
    public void integrate(int size, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        float averageWaterLevel = Math.max(0f, Math.min(1f, collectedWaterLevel / size));

        hotpotBlockEntity.getSoup().setWaterLevel(hotpotBlockEntity, pos, averageWaterLevel);
    }
}
