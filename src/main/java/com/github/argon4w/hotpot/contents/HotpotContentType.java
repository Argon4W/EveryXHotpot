package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.soups.IHotpotSoup;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface HotpotContentType<T extends IHotpotContent> {
    @NotNull
    T createContent();
}
