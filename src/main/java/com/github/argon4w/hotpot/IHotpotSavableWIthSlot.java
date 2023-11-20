package com.github.argon4w.hotpot;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface IHotpotSavableWIthSlot<T extends IHotpotSavableWIthSlot<?>> extends IHotpotSavable<T> {
    default CompoundNBT save(CompoundNBT compoundTag, byte slot) {
        compoundTag.putString("Type", getID());
        compoundTag.putByte("Slot", slot);

        return save(compoundTag);
    }

    static boolean isTagValid(CompoundNBT compoundTag) {
        return compoundTag.contains("Type", Constants.NBT.TAG_STRING) && compoundTag.contains("Slot", Constants.NBT.TAG_BYTE);
    }

    static void loadAll(ListNBT listTag, int size, Consumer<CompoundNBT> consumer) {
        listTag.stream().filter(tag -> tag instanceof CompoundNBT && isTagValid((CompoundNBT) tag) && ((CompoundNBT) tag).getByte("Slot") < size)
                .forEach(tag -> consumer.accept((CompoundNBT) tag));
    }

    static <U extends IHotpotSavableWIthSlot<?>> ListNBT saveAll(NonNullList<U> list) {
        return list.stream().map(savable -> savable.save(new CompoundNBT(), (byte) list.indexOf(savable))).collect(Collectors.toCollection(ListNBT::new));
    }
}
