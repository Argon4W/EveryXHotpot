package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;

public interface IHotpotSoupTypeWithActiveness extends IHotpotSoupType {
    float getActiveness();
    void setActiveness(float activeness);
}
