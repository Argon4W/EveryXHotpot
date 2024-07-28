package com.github.argon4w.hotpot.placements;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;

public interface IHotpotPlacementFactory<T extends IHotpotPlacement> {
    T buildFromSlots(int pos, Direction direction);
    MapCodec<T> buildFromCodec();
    boolean canPlace(int pos, Direction direction);
}
