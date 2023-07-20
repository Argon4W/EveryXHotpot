package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;

public interface IHotpotSoupWithActiveness extends IHotpotSoup {
    float getActiveness(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos);
    void setActiveness(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, float activeness);
}
