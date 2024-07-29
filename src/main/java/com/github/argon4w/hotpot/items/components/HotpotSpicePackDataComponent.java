package com.github.argon4w.hotpot.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.block.SuspiciousEffectHolder;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record HotpotSpicePackDataComponent(int charges, List<ItemStack> itemStacks) {
    public static final HotpotSpicePackDataComponent EMPTY = new HotpotSpicePackDataComponent(0, List.of());

    public static final Codec<HotpotSpicePackDataComponent> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                    Codec.INT.fieldOf("charges").forGetter(HotpotSpicePackDataComponent::charges),
                    ItemStack.CODEC.listOf().fieldOf("item_stacks").forGetter(HotpotSpicePackDataComponent::itemStacks)
            ).apply(data, HotpotSpicePackDataComponent::new))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSpicePackDataComponent> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
            StreamCodec.composite(
                    ByteBufCodecs.INT, HotpotSpicePackDataComponent::charges,
                    ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC), HotpotSpicePackDataComponent::itemStacks,
                    HotpotSpicePackDataComponent::new
            )
    );

    public HotpotSpicePackDataComponent setCharges(int charges) {
        return new HotpotSpicePackDataComponent(charges, List.copyOf(itemStacks));
    }

    public HotpotSpicePackDataComponent setItemStacks(List<ItemStack> itemStacks) {
        return new HotpotSpicePackDataComponent(charges, List.copyOf(itemStacks));
    }

    public HotpotSpicePackDataComponent addItemStack(ItemStack itemStack) {
        return itemStack.isEmpty() ? this : new HotpotSpicePackDataComponent(charges, Stream.concat(itemStacks.stream(), Stream.of(itemStack)).toList());
    }

    public List<MobEffectInstance> getFoodEffects() {
        return itemStacks.stream().map(this::getSuspiciousEffectHolder).map(SuspiciousEffectHolder::getSuspiciousEffects).map(SuspiciousStewEffects::effects).flatMap(Collection::stream).map(SuspiciousStewEffects.Entry::createEffectInstance).toList();
    }

    private SuspiciousEffectHolder getSuspiciousEffectHolder(ItemStack itemStack) {
        return SuspiciousEffectHolder.tryGet(itemStack.getItem());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotSpicePackDataComponent data && equalsItemStacks(data);
    }

    public boolean equalsItemStacks(HotpotSpicePackDataComponent another) {
        return itemStacks.size() == another.itemStacks().size() && IntStream.range(0, itemStacks.size()).allMatch(i -> ItemStack.isSameItemSameComponents(itemStacks.get(i), another.itemStacks.get(i)));
    }
}
