package com.github.argon4w.hotpot.soups;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface HotpotSoupType<T extends IHotpotSoup> {
    @NotNull
    T createSoup();
}
