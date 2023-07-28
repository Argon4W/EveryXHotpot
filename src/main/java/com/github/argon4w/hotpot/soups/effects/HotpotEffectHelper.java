package com.github.argon4w.hotpot.soups.effects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HotpotEffectHelper {
    public static void saveEffects(ItemStack itemStack, MobEffectInstance mobEffectInstance) {
        List<MobEffectInstance> effects = new ArrayList<>(itemStack.getOrCreateTag().getList("HotpotEffects", Tag.TAG_COMPOUND).stream()
                .map(tag -> (CompoundTag) tag)
                .map(MobEffectInstance::load)
                .filter(Objects::nonNull)
                .toList());
        mergeEffects(effects, mobEffectInstance);

        itemStack.getTag().put("HotpotEffects", effects.stream().map(effect -> effect.save(new CompoundTag())).collect(Collectors.toCollection(ListTag::new)));
    }

    public static boolean hasEffects(ItemStack itemStack) {
        return itemStack.hasTag() && itemStack.getTag().contains("HotpotEffects", Tag.TAG_LIST);
    }

    public static void listEffects(ItemStack itemStack, Consumer<MobEffectInstance> consumer) {
        if (!itemStack.hasTag()) return;

        itemStack.getTag().getList("HotpotEffects", Tag.TAG_COMPOUND).stream()
                .map(tag -> MobEffectInstance.load((CompoundTag) tag))
                .forEach(consumer);
    }

    public static List<MobEffectInstance> getListEffects(ItemStack itemStack) {
        List<MobEffectInstance> effects = new ArrayList<>();
        listEffects(itemStack, effects::add);

        return effects;
    }

    public static List<MobEffectInstance> mergeEffects(List<MobEffectInstance> effects) {
        ArrayList<MobEffectInstance> list = new ArrayList<>();
        effects.forEach(mobEffectInstance -> mergeEffects(list, mobEffectInstance));

        return list;
    }

    public static void mergeEffects(List<MobEffectInstance> effects, MobEffectInstance mobEffectInstance) {
        for (MobEffectInstance effect : effects) {
            if (effect.getEffect().equals(mobEffectInstance.getEffect())) {
                effect.update(mobEffectInstance);

                return;
            }
        }

        effects.add(mobEffectInstance);
    }
}
