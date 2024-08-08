package com.github.argon4w.hotpot.items.components;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.ArrayList;
import java.util.List;

public record HotpotFoodEffectsDataComponent(List<MobEffectInstance> effects) {
    public static final HotpotFoodEffectsDataComponent EMPTY = new HotpotFoodEffectsDataComponent(List.of());

    public static final Codec<HotpotFoodEffectsDataComponent> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                    MobEffectInstance.CODEC.listOf().fieldOf("effects").forGetter(HotpotFoodEffectsDataComponent::effects)
            ).apply(data, HotpotFoodEffectsDataComponent::new))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotFoodEffectsDataComponent> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
            StreamCodec.composite(
                    ByteBufCodecs.collection(ArrayList::new, MobEffectInstance.STREAM_CODEC), HotpotFoodEffectsDataComponent::effects,
                    HotpotFoodEffectsDataComponent::new
            )
    );

    public HotpotFoodEffectsDataComponent setEffects(List<MobEffectInstance> effects) {
        return new HotpotFoodEffectsDataComponent(List.copyOf(effects));
    }

    public HotpotFoodEffectsDataComponent addEffects(List<MobEffectInstance> newEffects) {
        return new HotpotFoodEffectsDataComponent(mergeEffects(new ArrayList<>(effects), List.copyOf(newEffects)));
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

    public static List<MobEffectInstance> getFoodEffects(ItemStack itemStack) {
        return getDataComponent(itemStack).effects().stream().map(MobEffectInstance::new).toList();
    }

    public static boolean hasFoodEffects(ItemStack itemStack) {
        return hasDataComponent(itemStack) && !getFoodEffects(itemStack).isEmpty();
    }

    public static ItemStack addEffects(ItemStack itemStack, List<MobEffectInstance> effects) {
        setDataComponent(itemStack, getDataComponent(itemStack).addEffects(effects));
        return itemStack;
    }

    public static List<MobEffectInstance> mergeEffects(List<MobEffectInstance> effects, List<MobEffectInstance> newEffects) {
        newEffects.forEach(newEffect -> effects.stream().filter(effect -> effect.is(newEffect.getEffect())).findFirst().ifPresentOrElse(effect -> effect.update(new MobEffectInstance(newEffect)), () -> effects.add(new MobEffectInstance(newEffect))));
        return effects;
    }
}
