package com.github.argon4w.hotpot.soups.effects;

import com.github.argon4w.hotpot.HotpotTagsHelper;
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
    public static void saveEffects(ItemStack itemStack, List<MobEffectInstance> effects) {
        effects.forEach(effect -> saveEffects(itemStack, effect));
    }

    public static void saveEffects(ItemStack itemStack, MobEffectInstance mobEffectInstance) {
        List<MobEffectInstance> effects = new ArrayList<>(HotpotTagsHelper.getHotpotTags(itemStack).getList("HotpotEffects", Tag.TAG_COMPOUND).stream()
                .map(tag -> (CompoundTag) tag)
                .map(MobEffectInstance::load)
                .filter(Objects::nonNull)
                .toList());
        mergeEffects(effects, mobEffectInstance);

        HotpotTagsHelper.updateHotpotTags(itemStack, "HotpotEffects", effects.stream()
                .map(HotpotEffectHelper::saveEffect)
                .collect(Collectors.toCollection(ListTag::new))
        );
    }

    public static Tag saveEffect(MobEffectInstance mobEffectInstance) {
        return mobEffectInstance.save();
    }

    public static boolean hasEffects(ItemStack itemStack) {
        return HotpotTagsHelper.hasHotpotTags(itemStack) && HotpotTagsHelper.getHotpotTags(itemStack).contains("HotpotEffects", Tag.TAG_LIST);
    }

    public static List<MobEffectInstance> getListEffects(ItemStack itemStack) {
        if (!hasEffects(itemStack)) {
            return List.of();
        }

        return HotpotTagsHelper.getHotpotTags(itemStack).getList("HotpotEffects", Tag.TAG_COMPOUND).stream()
                .map(tag -> (CompoundTag) tag)
                .map(MobEffectInstance::load)
                .toList();
    }

    public static List<MobEffectInstance> mergeEffects(List<MobEffectInstance> effects) {
        ArrayList<MobEffectInstance> list = new ArrayList<>();
        effects.forEach(mobEffectInstance -> mergeEffects(list, mobEffectInstance));

        return list;
    }

    public static void mergeEffects(List<MobEffectInstance> effects, MobEffectInstance mobEffectInstance) {
        for (MobEffectInstance effect : effects) {
            if (!effect.getEffect().equals(mobEffectInstance.getEffect())) {
                continue;
            }

            effect.update(mobEffectInstance);
            return;
        }

        effects.add(mobEffectInstance);
    }
}
