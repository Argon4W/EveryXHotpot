package com.github.argon4w.hotpot.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record HotpotNapkinHolderDataComponent(ItemStack itemStack) {
    public static final HotpotNapkinHolderDataComponent EMPTY = new HotpotNapkinHolderDataComponent(ItemStack.EMPTY);

    public static final Codec<HotpotNapkinHolderDataComponent> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                    ItemStack.OPTIONAL_CODEC.fieldOf("napkin_item_slot").forGetter(HotpotNapkinHolderDataComponent::itemStack)
            ).apply(data, HotpotNapkinHolderDataComponent::new))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotNapkinHolderDataComponent> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
            StreamCodec.composite(
                    ItemStack.OPTIONAL_STREAM_CODEC, HotpotNapkinHolderDataComponent::itemStack,
                    HotpotNapkinHolderDataComponent::new
            )
    );

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotNapkinHolderDataComponent data && ItemStack.isSameItemSameComponents(itemStack, data.itemStack);
    }
}
