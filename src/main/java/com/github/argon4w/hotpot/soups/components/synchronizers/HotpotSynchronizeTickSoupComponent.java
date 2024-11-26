package com.github.argon4w.hotpot.soups.components.synchronizers;

import com.github.argon4w.fancytoys.blocks.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import java.util.Optional;

public class HotpotSynchronizeTickSoupComponent extends AbstractHotpotSoupComponent {

    @Override
    public Optional<IHotpotSoupSyncData> getSoupComponenentSyncData(
            HotpotBlockEntity hotpotBlockEntity,
            HotpotComponentSoup soup,
            LevelBlockPos pos) {
        return Optional.of(new SyncData());
    }

    public static class SyncData implements IHotpotSoupSyncData {

        @Override
        public void collect(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {

        }

        @Override
        public void apply(int size, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos) {
            soup.onTick(hotpotBlockEntity, pos);
        }

        @Override
        public boolean shouldApply() {
            return true;
        }
    }
}
