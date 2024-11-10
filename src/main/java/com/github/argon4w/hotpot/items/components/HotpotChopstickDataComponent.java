package com.github.argon4w.hotpot.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record HotpotChopstickDataComponent(ItemStack itemStack) {

    public static final HotpotChopstickDataComponent EMPTY = new HotpotChopstickDataComponent(ItemStack.EMPTY);

    public static final Codec<HotpotChopstickDataComponent> CODEC = Codec.lazyInitialized(() -> ItemStack.OPTIONAL_CODEC
            .fieldOf("item_stack")
            .xmap(HotpotChopstickDataComponent::new, HotpotChopstickDataComponent::itemStack)
            .codec());

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotChopstickDataComponent> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> ItemStack.OPTIONAL_STREAM_CODEC
            .map(HotpotChopstickDataComponent::new, HotpotChopstickDataComponent::itemStack));

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotChopstickDataComponent data
                && ItemStack.isSameItemSameComponents(itemStack, data.itemStack)
                && itemStack.getCount() == data.itemStack.getCount();
    }
}
