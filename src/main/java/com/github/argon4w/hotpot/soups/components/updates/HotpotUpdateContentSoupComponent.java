package com.github.argon4w.hotpot.soups.components.updates;

import com.github.argon4w.hotpot.IHotpotResult;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.components.AbstractHotpotSoupComponent;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;

public class HotpotUpdateContentSoupComponent extends AbstractHotpotSoupComponent {
    @Override
    public IHotpotResult<IHotpotContent> onContentUpdate(HotpotBlockEntity hotpotBlockEntity, HotpotComponentSoup soup, LevelBlockPos pos, IHotpotResult<IHotpotContent> result) {
        return result.ifPresent(content -> hotpotBlockEntity.getContents().stream().filter(content1 -> content1 != content).forEach(content1 -> content1.onContentUpdate(content, hotpotBlockEntity, pos)));
    }
}
