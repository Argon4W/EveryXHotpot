package com.github.argon4w.hotpot.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public record HotpotSkewerDataComponent(List<ItemStack> itemStacks) {
    public static final HotpotSkewerDataComponent EMPTY = new HotpotSkewerDataComponent(new ArrayList<>());

    public static final Codec<HotpotSkewerDataComponent> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                    ItemStack.CODEC.listOf().fieldOf("item_stacks").forGetter(HotpotSkewerDataComponent::itemStacks)
            ).apply(data, HotpotSkewerDataComponent::new))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSkewerDataComponent> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
            StreamCodec.composite(
                    ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC), HotpotSkewerDataComponent::itemStacks,
                    HotpotSkewerDataComponent::new
            )
    );

    public HotpotSkewerDataComponent setItemStacks(List<ItemStack> itemStacks) {
        return new HotpotSkewerDataComponent(List.copyOf(itemStacks));
    }

    public HotpotSkewerDataComponent applyItemStacks(Consumer<ItemStack> consumer) {
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
