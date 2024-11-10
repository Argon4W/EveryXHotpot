package com.github.argon4w.hotpot.items.components;

import com.github.argon4w.fancytoys.MobEffectMap;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record HotpotFoodEffectsDataComponent(MobEffectMap effects) {

    public static final HotpotFoodEffectsDataComponent EMPTY = new HotpotFoodEffectsDataComponent(new MobEffectMap());

    public static final Codec<HotpotFoodEffectsDataComponent> CODEC = Codec.lazyInitialized(() -> MobEffectMap.CODEC
            .fieldOf("effects")
            .xmap(HotpotFoodEffectsDataComponent::new, HotpotFoodEffectsDataComponent::effects)
            .codec());

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotFoodEffectsDataComponent> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> MobEffectMap.STREAM_CODEC
            .map(HotpotFoodEffectsDataComponent::new, HotpotFoodEffectsDataComponent::effects));

    public HotpotFoodEffectsDataComponent addEffects(MobEffectMap newEffects) {
        return effects.isEmpty()
                ? new HotpotFoodEffectsDataComponent(newEffects.copy())
                : new HotpotFoodEffectsDataComponent(effects.copy().putEffects(newEffects));
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

    public static void addEffects(ItemStack itemStack, MobEffectMap effects) {
        setDataComponent(itemStack, getDataComponent(itemStack).addEffects(effects));
    }
}
