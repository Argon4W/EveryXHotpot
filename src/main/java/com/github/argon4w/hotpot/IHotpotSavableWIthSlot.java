package com.github.argon4w.hotpot;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.function.Consumer;

public interface IHotpotSavableWIthSlot<T extends IHotpotSavableWIthSlot<?>> extends IHotpotSavable<T> {
    static void loadAll(ListTag listTag, int size, Consumer<CompoundTag> consumer) {
        for (Tag tag : listTag) {
            if (!(tag instanceof CompoundTag compoundTag)) {
                continue;
            }

            if (!compoundTag.contains("Type", Tag.TAG_STRING)) {
                continue;
            }

            if (!compoundTag.contains("Slot", Tag.TAG_BYTE)) {
                continue;
            }

            if (compoundTag.getByte("Slot") >= size) {
                continue;
            }

            consumer.accept(compoundTag);
        }
    }

    static ListTag saveAll(NonNullList<? extends IHotpotSavableWIthSlot<?>> list) {
        ListTag listTag = new ListTag();

        for (int i = 0; i < list.size(); i ++) {
            IHotpotSavableWIthSlot<?> savable = list.get(i);
            CompoundTag compoundTag = new CompoundTag();

            compoundTag.putString("Type", savable.getResourceLocation().toString());
            compoundTag.putByte("Slot", (byte) i);

            listTag.add(savable.save(compoundTag));
        }

        return listTag;
    }
}
