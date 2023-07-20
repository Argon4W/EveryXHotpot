package com.github.argon4w.hotpot;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface IHotpotSavableWIthSlot<T extends IHotpotSavableWIthSlot<?>> extends IHotpotSavable<T> {
    default CompoundTag save(CompoundTag compoundTag, byte slot) {
        compoundTag.putString("Type", getID());
        compoundTag.putByte("Slot", slot);

        return save(compoundTag);
    }

    static boolean isTagValid(CompoundTag compoundTag) {
        return compoundTag.contains("Type", Tag.TAG_STRING) && compoundTag.contains("Slot", Tag.TAG_BYTE);
    }

    static void loadAll(ListTag listTag, int size, Consumer<CompoundTag> consumer) {
        listTag.stream().filter(tag -> tag instanceof CompoundTag compoundTag && isTagValid(compoundTag) && compoundTag.getByte("Slot") < size)
                .forEach(tag -> consumer.accept((CompoundTag) tag));
    }

    static <U extends IHotpotSavableWIthSlot<?>> ListTag saveAll(NonNullList<U> list) {
        return list.stream().map(savable -> savable.save(new CompoundTag(), (byte) list.indexOf(savable))).collect(Collectors.toCollection(ListTag::new));
    }
}
