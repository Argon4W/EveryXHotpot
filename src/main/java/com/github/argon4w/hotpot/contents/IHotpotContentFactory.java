package com.github.argon4w.hotpot.contents;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface IHotpotContentFactory<T extends IHotpotContent> {
    @NotNull
    T build();
}
