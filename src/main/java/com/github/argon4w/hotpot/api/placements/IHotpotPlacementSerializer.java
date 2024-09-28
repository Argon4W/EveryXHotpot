package com.github.argon4w.hotpot.api.placements;

import com.github.argon4w.hotpot.placements.coords.ComplexDirection;
import com.mojang.serialization.MapCodec;

import java.util.List;
import java.util.Optional;

public interface IHotpotPlacementSerializer<T extends IHotpotPlacement> {
    MapCodec<T> getCodec();
    T get(List<Integer> positions, ComplexDirection direction);
    List<Optional<Integer>> getPositions(int position, ComplexDirection direction);
}
