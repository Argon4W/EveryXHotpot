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
import java.util.stream.Stream;

public record HotpotStrainerBasketDataComponent(List<ItemStack> itemStacks) {
    public static final HotpotStrainerBasketDataComponent EMPTY = new HotpotStrainerBasketDataComponent(new ArrayList<>());

    public static final Codec<HotpotStrainerBasketDataComponent> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                    ItemStack.CODEC.listOf().fieldOf("item_stacks").forGetter(HotpotStrainerBasketDataComponent::itemStacks)
            ).apply(data, HotpotStrainerBasketDataComponent::new))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotStrainerBasketDataComponent> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
            StreamCodec.composite(
                    ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC), HotpotStrainerBasketDataComponent::itemStacks,
                    HotpotStrainerBasketDataComponent::new
            )
    );

    public HotpotStrainerBasketDataComponent setItemStacks(List<ItemStack> itemStacks) {
        return new HotpotStrainerBasketDataComponent(List.copyOf(itemStacks));
    }

    public HotpotStrainerBasketDataComponent addItemStack(ItemStack itemStack) {
        return itemStack.isEmpty() ? this : new HotpotStrainerBasketDataComponent(Stream.concat(itemStacks.stream(), Stream.of(itemStack)).toList());
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotStrainerBasketDataComponent data && ItemStack.listMatches(itemStacks, data.itemStacks);
    }
}
