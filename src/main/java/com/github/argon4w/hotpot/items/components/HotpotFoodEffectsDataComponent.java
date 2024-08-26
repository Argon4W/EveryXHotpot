package com.github.argon4w.hotpot.items.components;

import com.github.argon4w.hotpot.HotpotMobEffectMap;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.List;

public record HotpotFoodEffectsDataComponent(HotpotMobEffectMap effects) {
    public static final HotpotFoodEffectsDataComponent EMPTY = new HotpotFoodEffectsDataComponent(new HotpotMobEffectMap());

    public static final Codec<HotpotFoodEffectsDataComponent> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                    HotpotMobEffectMap.CODEC.fieldOf("effects").forGetter(HotpotFoodEffectsDataComponent::effects)
            ).apply(data, HotpotFoodEffectsDataComponent::new))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotFoodEffectsDataComponent> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
            StreamCodec.composite(
                    HotpotMobEffectMap.STREAM_CODEC, HotpotFoodEffectsDataComponent::effects,
                    HotpotFoodEffectsDataComponent::new
            )
    );

    public HotpotFoodEffectsDataComponent addEffects(HotpotMobEffectMap newEffects) {
        return effects.isEmpty() ? new HotpotFoodEffectsDataComponent(newEffects.copy()) : new HotpotFoodEffectsDataComponent(effects.copy().putEffects(newEffects));
    }

    public static boolean hasDataComponent(ItemStack itemStack) {
        return itemStack.has(HotpotModEntry.HOTPOT_FOOD_EFFECTS_DATA_COMPONENT);
    }

    public static HotpotFoodEffectsDataComponent getDataComponent(ItemStack itemStack) {
        return itemStack.getOrDefault(HotpotModEntry.HOTPOT_FOOD_EFFECTS_DATA_COMPONENT, HotpotFoodEffectsDataComponent.EMPTY);
    }

    public static void setDataComponent(ItemStack itemStack, HotpotFoodEffectsDataComponent dataComponent) {
        itemStack.set(HotpotModEntry.HOTPOT_FOOD_EFFECTS_DATA_COMPONENT, dataComponent);
    }

    public static List<MobEffectInstance> getEffects(ItemStack itemStack) {
        return getDataComponent(itemStack).effects().getMobEffects();
    }

    public static boolean hasEffects(ItemStack itemStack) {
        return hasDataComponent(itemStack) && !getDataComponent(itemStack).effects.isEmpty();
    }

    public static void addEffects(ItemStack itemStack, HotpotMobEffectMap effects) {
        setDataComponent(itemStack, getDataComponent(itemStack).addEffects(effects));
    }
}
