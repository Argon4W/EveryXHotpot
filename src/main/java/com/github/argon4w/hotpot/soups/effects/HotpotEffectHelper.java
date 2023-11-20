package com.github.argon4w.hotpot.soups.effects;

import com.github.argon4w.hotpot.HotpotTagsHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HotpotEffectHelper {
    public static void saveEffects(ItemStack itemStack, EffectInstance effectInstance) {
        List<EffectInstance> effects = new ArrayList<>(HotpotTagsHelper.getHotpotTag(itemStack).getList("HotpotEffects", Constants.NBT.TAG_COMPOUND).stream()
                .map(tag -> (CompoundNBT) tag)
                .map(EffectInstance::load)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        mergeEffects(effects, effectInstance);

        HotpotTagsHelper.updateHotpotTag(itemStack, compoundTag -> compoundTag.put("HotpotEffects", effects.stream().map(effect -> effect.save(new CompoundNBT())).collect(Collectors.toCollection(ListNBT::new))));
    }

    public static boolean hasEffects(ItemStack itemStack) {
        return HotpotTagsHelper.hasHotpotTag(itemStack) && HotpotTagsHelper.getHotpotTag(itemStack).contains("HotpotEffects", Constants.NBT.TAG_LIST);
    }

    public static void listEffects(ItemStack itemStack, Consumer<EffectInstance> consumer) {
        if (!hasEffects(itemStack)) return;

        HotpotTagsHelper.getHotpotTag(itemStack).getList("HotpotEffects", Constants.NBT.TAG_COMPOUND).stream()
                .map(tag -> EffectInstance.load((CompoundNBT) tag))
                .forEach(consumer);
    }

    public static List<EffectInstance> getListEffects(ItemStack itemStack) {
        List<EffectInstance> effects = new ArrayList<>();
        listEffects(itemStack, effects::add);

        return effects;
    }

    public static List<EffectInstance> mergeEffects(List<EffectInstance> effects) {
        ArrayList<EffectInstance> list = new ArrayList<>();
        effects.forEach(EffectInstance -> mergeEffects(list, EffectInstance));

        return list;
    }

    public static void mergeEffects(List<EffectInstance> effects, EffectInstance EffectInstance) {
        for (EffectInstance effect : effects) {
            if (effect.getEffect().equals(EffectInstance.getEffect())) {
                effect.update(EffectInstance);

                return;
            }
        }

        effects.add(EffectInstance);
    }
}
