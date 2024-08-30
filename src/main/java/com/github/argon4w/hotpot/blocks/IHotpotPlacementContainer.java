package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.placements.IHotpotPlacement;

import java.util.List;

public interface IHotpotPlacementContainer extends IHotpotTablewareContainer {
    boolean canBeRemoved();
    boolean isInfiniteContent();
    boolean isPositionValid(int position, int layer);
    List<Integer> getOccupiedPositions(int layer);
    int getLayerOffset();
    void place(IHotpotPlacement placement, int position, int layer);
}
