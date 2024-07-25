package com.github.argon4w.hotpot.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record HotpotChopstickDataComponent(ItemStack itemStack) {
    public static final Codec<HotpotChopstickDataComponent> CODEC = RecordCodecBuilder.create(data -> data.group(
            ItemStack.CODEC.fieldOf("item_stack").forGetter(HotpotChopstickDataComponent::itemStack)
    ).apply(data, HotpotChopstickDataComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotChopstickDataComponent> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, HotpotChopstickDataComponent::itemStack,
            HotpotChopstickDataComponent::new
    );
}
