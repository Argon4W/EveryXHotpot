package com.github.argon4w.hotpot.blocks;

import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface IHotpotPlacementContainer extends IHotpotTablewareContainer {
    boolean canBeRemoved();
    boolean canConsumeContents();
    List<Integer> getProvidedPositions(int layer);
    List<Integer> getOccupiedPositions(int layer);
    int getLayer(Vec3 vec3);
    void place(IHotpotPlacement placement, int position, int layer);
}
