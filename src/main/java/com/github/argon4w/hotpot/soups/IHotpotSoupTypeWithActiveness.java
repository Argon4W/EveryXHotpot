package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;

public interface IHotpotSoupTypeWithActiveness extends IHotpotSoupType {
    float getActiveness(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos);
    void setActiveness(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, float activeness);
}
