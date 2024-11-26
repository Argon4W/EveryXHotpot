package com.github.argon4w.hotpot.soups.components.synchronizers;

import com.github.argon4w.fancytoys.blocks.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;

public interface IHotpotSoupSyncData {

    void collect(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos);
    void apply(int size, HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos);
    boolean shouldApply();
}
