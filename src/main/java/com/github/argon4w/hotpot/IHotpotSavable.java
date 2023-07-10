package com.github.argon4w.hotpot;

import net.minecraft.nbt.CompoundTag;

public interface IHotpotSavable {
    void load(CompoundTag compoundTag);
    CompoundTag save(CompoundTag compoundTag);

    boolean isValid(CompoundTag compoundTag);
    String getID();
}
