package com.github.argon4w.hotpot.placeables;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface HotpotPlaceableType<T extends IHotpotPlaceable> {
    @NotNull
    T createPlaceable();
}
