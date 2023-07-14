package com.github.argon4w.hotpot;

import net.minecraft.nbt.CompoundTag;

import java.util.function.Supplier;

public interface IHotpotSavable {
    void load(CompoundTag compoundTag);
    CompoundTag save(CompoundTag compoundTag);
    boolean isValid(CompoundTag compoundTag);
    String getID();

    static <T extends IHotpotSavable> T loadOrElseGet(T savable, CompoundTag compoundTag, Supplier<T> supplier) {
        if (savable.isValid(compoundTag)) {
            savable.load(compoundTag);

            return savable;
        } else {
            return supplier.get();
        }
    }
}
