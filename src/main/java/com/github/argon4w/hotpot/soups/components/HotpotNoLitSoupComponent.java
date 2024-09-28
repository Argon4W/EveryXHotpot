package com.github.argon4w.hotpot.soups.components;

import com.github.argon4w.hotpot.api.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;

public class HotpotNoLitSoupComponent extends AbstractHotpotSoupComponent {
    @Override
    public IHotpotResult<Boolean> getHotpotLit(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<Boolean> result) {
        return IHotpotResult.success(false);
    }
}
