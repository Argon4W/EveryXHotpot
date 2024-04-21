package com.github.argon4w.hotpot;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class HotpotTagsHelper {
    public static boolean hasHotpotTags(ItemStack itemStack) {
        return itemStack.hasTag() && itemStack.getTag().contains(HotpotModEntry.TAG_LOCATION.toString(), Tag.TAG_COMPOUND);
    }

    public static CompoundTag getHotpotTags(ItemStack itemStack) {
        return hasHotpotTags(itemStack) ? itemStack.getTag().getCompound(HotpotModEntry.TAG_LOCATION.toString()) : new CompoundTag();
    }

    public static void setHotpotTags(ItemStack itemStack, CompoundTag compoundTag) {
        itemStack.getOrCreateTag().put(HotpotModEntry.TAG_LOCATION.toString(), compoundTag);
    }

    public static void updateHotpotTags(ItemStack itemStack, String key, Tag tag){
        CompoundTag hotpotTag = getHotpotTags(itemStack);
        hotpotTag.put(key, tag);

        setHotpotTags(itemStack, hotpotTag);
    }

    public static CompoundTag saveItemStack(ItemStack itemStack) {
        return itemStack.save(new CompoundTag());
    }
}
