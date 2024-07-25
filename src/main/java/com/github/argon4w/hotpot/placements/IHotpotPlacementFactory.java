package com.github.argon4w.hotpot.placements;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public interface IHotpotPlacementFactory<T extends IHotpotPlacement> {
    T buildFromSlots(int pos, Direction direction, HolderLookup.Provider registryAccess);
    T buildFromTag(CompoundTag compoundTag, HolderLookup.Provider registryAccess);
    boolean isValid(CompoundTag compoundTag, HolderLookup.Provider registryAccess);
    boolean canPlace(int pos, Direction direction);
}
