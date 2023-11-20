package com.github.argon4w.hotpot;


import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

import java.util.function.Consumer;

public class HotpotTagsHelper {
    public static boolean hasHotpotTag(ItemStack itemStack) {
        return itemStack.hasTag() && itemStack.getTag().contains(HotpotModEntry.TAG_LOCATION.toString(), Constants.NBT.TAG_COMPOUND);
    }

    public static CompoundNBT getHotpotTag(ItemStack itemStack) {
        return hasHotpotTag(itemStack) ? itemStack.getTag().getCompound(HotpotModEntry.TAG_LOCATION.toString()) : new CompoundNBT();
    }

    public static void setHotpotTag(ItemStack itemStack, CompoundNBT compoundTag) {
        itemStack.getOrCreateTag().put(HotpotModEntry.TAG_LOCATION.toString(), compoundTag);
    }

    public static void updateHotpotTag(ItemStack itemStack, Consumer<CompoundNBT> consumer) {
        CompoundNBT hotpotTag = hasHotpotTag(itemStack) ? getHotpotTag(itemStack) : new CompoundNBT();
        consumer.accept(hotpotTag);

        setHotpotTag(itemStack, hotpotTag);
    }
}
