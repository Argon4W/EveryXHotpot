package com.github.argon4w.hotpot.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record HotpotChopstickDataComponent(ItemStack itemStack) {
    public static final HotpotChopstickDataComponent EMPTY = new HotpotChopstickDataComponent(ItemStack.EMPTY);

    public static final Codec<HotpotChopstickDataComponent> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                    ItemStack.OPTIONAL_CODEC.fieldOf("item_stack").forGetter(HotpotChopstickDataComponent::itemStack)
            ).apply(data, HotpotChopstickDataComponent::new))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotChopstickDataComponent> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
            StreamCodec.composite(
                    ItemStack.STREAM_CODEC, HotpotChopstickDataComponent::itemStack,
                    HotpotChopstickDataComponent::new
            )
    );

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotChopstickDataComponent data && ItemStack.isSameItemSameComponents(itemStack, data.itemStack) && itemStack.getCount() == data.itemStack.getCount();
    }
}
