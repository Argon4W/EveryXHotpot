package com.github.argon4w.hotpot.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record HotpotSkewerDataComponent(List<ItemStack> itemStacks) {

    public static final HotpotSkewerDataComponent EMPTY = new HotpotSkewerDataComponent(new ArrayList<>());

    public static final Codec<HotpotSkewerDataComponent> CODEC = Codec.lazyInitialized(() -> ItemStack.CODEC.listOf()
            .fieldOf("item_stacks")
            .xmap(HotpotSkewerDataComponent::new, HotpotSkewerDataComponent::itemStacks)
            .codec());

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSkewerDataComponent> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> ItemStack.STREAM_CODEC
            .apply(ByteBufCodecs.list())
            .map(HotpotSkewerDataComponent::new, HotpotSkewerDataComponent::itemStacks));

    public HotpotSkewerDataComponent setItemStacks(List<ItemStack> itemStacks) {
        return new HotpotSkewerDataComponent(List.copyOf(itemStacks));
    }

    public HotpotSkewerDataComponent applyToItemStacks(Consumer<ItemStack> consumer) {
        return new HotpotSkewerDataComponent(itemStacks.stream().map(ItemStack::copy).peek(consumer).toList());
    }

    public HotpotSkewerDataComponent addItemStack(ItemStack itemStack) {
        return itemStack.isEmpty() ? this : new HotpotSkewerDataComponent(Stream.concat(itemStacks.stream(), Stream.of(itemStack)).toList());
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotSkewerDataComponent data && ItemStack.listMatches(itemStacks, data.itemStacks);
    }
}
