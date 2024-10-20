package com.github.argon4w.hotpot.soups.components.synchronizers;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;

import java.util.Optional;

public class HotpotSynchronizeWaterLevelSoupComponent extends AbstractHotpotSoupComponent {
    @Override
    public Optional<IHotpotSoupComponentSynchronizer> getSoupComponentSynchronizer(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
        return Optional.of(new Synchronizer());
    }

    public static class Synchronizer implements IHotpotSoupComponentSynchronizer {
        private double totalWaterLevel;

        public Synchronizer() {
            this.totalWaterLevel = 0;
        }

        @Override
        public void collect(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
            totalWaterLevel += soup.getOverflowWaterLevel() + soup.getWaterLevel();
            soup.onDiscardOverflowWaterLevel(hotpotBlockEntity, pos);
        }

        @Override
        public void apply(int size, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
            soup.setWaterLevelWithOverflow(Math.clamp(totalWaterLevel / size, 0.0, 1.0), hotpotBlockEntity, pos);
        }

        @Override
        public boolean shouldApply() {
            return true;
        }
    }
}
