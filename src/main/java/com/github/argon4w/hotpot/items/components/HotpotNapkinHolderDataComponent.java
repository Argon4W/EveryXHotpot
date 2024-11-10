package com.github.argon4w.hotpot.items.components;

import com.github.argon4w.fancytoys.LevelBlockPos;
import com.github.argon4w.fancytoys.SimpleItemSlot;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record HotpotNapkinHolderDataComponent(SimpleItemSlot itemSlot) {

    public static final HotpotNapkinHolderDataComponent EMPTY = new HotpotNapkinHolderDataComponent(new SimpleItemSlot());

    public static final Codec<HotpotNapkinHolderDataComponent> CODEC = Codec.lazyInitialized(() -> SimpleItemSlot.CODEC
            .fieldOf("item_slot")
            .xmap(HotpotNapkinHolderDataComponent::new, HotpotNapkinHolderDataComponent::itemSlot)
            .codec());

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotNapkinHolderDataComponent> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> SimpleItemSlot.STREAM_CODEC
            .map(HotpotNapkinHolderDataComponent::new, HotpotNapkinHolderDataComponent::itemSlot));

    public HotpotNapkinHolderDataComponent dropNapkinItemSlot(LevelBlockPos pos) {
        return new HotpotNapkinHolderDataComponent(itemSlot().copy().dropItem(pos));
    }

    public HotpotNapkinHolderDataComponent addNapkinItemSlot(ItemStack itemStack) {
        return itemStack.is(Items.PAPER)
                ? new HotpotNapkinHolderDataComponent(itemSlot.copy().transferItem(itemStack))
                : this;
    }

    public HotpotNapkinHolderDataComponent shrinkNapkinItemSlot(boolean consume) {
        return new HotpotNapkinHolderDataComponent(itemSlot.copy().shrink(consume ? 1 : 0));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotNapkinHolderDataComponent data && itemSlot.equals(data.itemSlot);
    }
}
