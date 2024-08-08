package com.github.argon4w.hotpot.soups.types;

import com.github.argon4w.hotpot.soups.IHotpotSoup;

public interface IHotpotSoupWithActiveness extends IHotpotSoup {
    float getActiveness();
    void setActiveness(float activeness);
}
