package com.github.argon4w.hotpot;

import net.minecraft.nbt.CompoundNBT;

import java.util.function.Supplier;

public interface IHotpotSavable<T extends IHotpotSavable<?>> {
    T load(CompoundNBT compoundTag);
    CompoundNBT save(CompoundNBT compoundTag);
    boolean isValid(CompoundNBT compoundTag);
    String getID();

    default T loadOrElseGet(CompoundNBT compoundTag, Supplier<T> supplier) {
        return isValid(compoundTag) ? load(compoundTag) : supplier.get();
    }
}
