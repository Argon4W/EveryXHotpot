package com.github.argon4w.hotpot.placements;

import com.github.argon4w.hotpot.placements.coords.ComplexDirection;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public interface IHotpotPlacementSerializer<T extends IHotpotPlacement> {
    MapCodec<T> getCodec();
    T get(List<Integer> positions, ComplexDirection direction);
    List<Optional<Integer>> getPositions(int position, ComplexDirection direction);
}
