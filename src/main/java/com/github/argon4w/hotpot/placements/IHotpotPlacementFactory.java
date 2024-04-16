package com.github.argon4w.hotpot.placements;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface IHotpotPlacementFactory<T extends IHotpotPlacement> {
    @NotNull
    T build();
}
