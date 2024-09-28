package com.github.argon4w.hotpot.api.blocks;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.placements.IHotpotPlacement;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface IHotpotPlacementContainer extends IHotpotTablewareContainer {
    boolean canBeRemoved();
    boolean canConsumeContents();
    List<Integer> getProvidedPositions(int layer, LevelBlockPos pos);
    List<Integer> getOccupiedPositions(int layer, LevelBlockPos pos);
    int getLayer(Vec3 vec3);
    void place(IHotpotPlacement placement, int position, int layer, LevelBlockPos pos);
}
