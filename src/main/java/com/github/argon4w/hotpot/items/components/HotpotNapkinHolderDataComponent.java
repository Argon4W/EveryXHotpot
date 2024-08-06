package com.github.argon4w.hotpot.items.components;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.placements.SimpleItemSlot;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record HotpotNapkinHolderDataComponent(SimpleItemSlot napkinItemSlot) {
    public static final HotpotNapkinHolderDataComponent EMPTY = new HotpotNapkinHolderDataComponent(new SimpleItemSlot(ItemStack.EMPTY));

    public static final Codec<HotpotNapkinHolderDataComponent> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                    SimpleItemSlot.CODEC.fieldOf("napkin_item_slot").forGetter(HotpotNapkinHolderDataComponent::napkinItemSlot)
            ).apply(data, HotpotNapkinHolderDataComponent::new))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotNapkinHolderDataComponent> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
            StreamCodec.composite(
                    SimpleItemSlot.STREAM_CODEC, HotpotNapkinHolderDataComponent::napkinItemSlot,
                    HotpotNapkinHolderDataComponent::new
            )
    );

    public HotpotNapkinHolderDataComponent addItemStack(ItemStack itemStack) {
        SimpleItemSlot napkinItemSlot = this.napkinItemSlot.copy();
        napkinItemSlot.addItem(itemStack);
        return new HotpotNapkinHolderDataComponent(napkinItemSlot);
    }

    public HotpotNapkinHolderDataComponent shrinkItemStack() {
        SimpleItemSlot napkinItemSlot = this.napkinItemSlot.copy();
        napkinItemSlot.getItemStack().shrink(1);
        return new HotpotNapkinHolderDataComponent(napkinItemSlot);
    }


    public HotpotNapkinHolderDataComponent dropItemStack(LevelBlockPos pos) {
        SimpleItemSlot napkinItemSlot = this.napkinItemSlot.copy();
        napkinItemSlot.dropItem(pos);
        return new HotpotNapkinHolderDataComponent(napkinItemSlot);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotNapkinHolderDataComponent data && napkinItemSlot.equals(data.napkinItemSlot);
    }
}
