package com.github.argon4w.hotpot.spice;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class SpiceEffectHelper {
    public static void saveEffects(ItemStack itemStack, MobEffectInstance mobEffectInstance) {
        ListTag list = itemStack.getOrCreateTag().getList("SpiceEffects", Tag.TAG_COMPOUND);
        list.add(mobEffectInstance.save(new CompoundTag()));

        itemStack.getTag().put("SpiceEffects", list);
    }

    public static void listEffects(ItemStack itemStack, Consumer<MobEffectInstance> consumer) {
        if (!itemStack.hasTag()) return;

        itemStack.getTag().getList("SpiceEffects", Tag.TAG_COMPOUND).stream()
                .map(tag -> MobEffectInstance.load((CompoundTag) tag))
                .forEach(consumer);
    }
}
