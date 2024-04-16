package com.github.argon4w.hotpot;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface IHotpotSavable<T extends IHotpotSavable<?>> {
    T load(CompoundTag compoundTag);
    CompoundTag save(CompoundTag compoundTag);
    boolean isValid(CompoundTag compoundTag);
    ResourceLocation getResourceLocation();

    default T loadOrElseGet(CompoundTag compoundTag, Supplier<T> supplier) {
        return isValid(compoundTag) ? load(compoundTag) : supplier.get();
    }
}
