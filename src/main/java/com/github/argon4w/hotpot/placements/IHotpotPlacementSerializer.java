package com.github.argon4w.hotpot.placements;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;

public interface IHotpotPlacementSerializer<T extends IHotpotPlacement> {
    MapCodec<T> getCodec();
    T get(int pos, Direction direction);
    boolean canPlace(int pos, Direction direction);
}
