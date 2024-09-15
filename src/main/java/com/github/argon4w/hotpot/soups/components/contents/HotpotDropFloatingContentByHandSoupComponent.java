package com.github.argon4w.hotpot.soups.components.contents;

import com.github.argon4w.hotpot.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;

public class HotpotDropFloatingContentByHandSoupComponent extends AbstractHotpotSoupComponent {
    @Override
    public IHotpotResult<IHotpotContent> getContentResultByHand(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<IHotpotContent> result) {
        return result.consume(content -> pos.dropCopiedFloatingItemStacks(content.getContentResultItemStacks(hotpotBlockEntity, pos)));
    }
}
