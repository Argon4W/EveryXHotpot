package com.github.argon4w.hotpot;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class HotpotTagsHelper {
    public static boolean hasHotpotTag(ItemStack itemStack) {
        return itemStack.hasTag() && itemStack.getTag().contains(HotpotModEntry.TAG_LOCATION.toString(), Tag.TAG_COMPOUND);
    }

    public static CompoundTag getHotpotTag(ItemStack itemStack) {
        return hasHotpotTag(itemStack) ? itemStack.getTag().getCompound(HotpotModEntry.TAG_LOCATION.toString()) : new CompoundTag();
    }

    public static void setHotpotTag(ItemStack itemStack, CompoundTag compoundTag) {
        itemStack.getOrCreateTag().put(HotpotModEntry.TAG_LOCATION.toString(), compoundTag);
    }

    public static void updateHotpotTag(ItemStack itemStack, Consumer<CompoundTag> consumer) {
        CompoundTag hotpotTag = hasHotpotTag(itemStack) ? getHotpotTag(itemStack) : new CompoundTag();
        consumer.accept(hotpotTag);

        setHotpotTag(itemStack, hotpotTag);
    }
}
