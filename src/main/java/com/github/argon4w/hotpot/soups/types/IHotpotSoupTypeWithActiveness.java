package com.github.argon4w.hotpot.soups.types;

import com.github.argon4w.hotpot.soups.IHotpotSoupType;

public interface IHotpotSoupTypeWithActiveness extends IHotpotSoupType {
    float getActiveness();
    void setActiveness(float activeness);
}
