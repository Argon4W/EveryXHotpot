package com.github.argon4w.hotpot.items.components;

import com.github.argon4w.hotpot.soups.HotpotSoupTypeHolder;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeSerializers;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.ArrayList;
import java.util.List;

public record HotpotPaperBowlDataComponent(HotpotSoupTypeHolder<?> soupTypeHolder, boolean drained, List<ItemStack> items, List<ItemStack> skewers) {
    public static final HotpotPaperBowlDataComponent EMPTY = new HotpotPaperBowlDataComponent(HotpotSoupTypeSerializers.getEmptySoupTypeHolder(), false, List.of(), List.of());

    public static final Codec<HotpotPaperBowlDataComponent> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                    HotpotSoupTypeSerializers.getHolderCodec().fieldOf("soup_type").forGetter(HotpotPaperBowlDataComponent::soupTypeHolder),
                    Codec.BOOL.fieldOf("soup_drained").forGetter(HotpotPaperBowlDataComponent::drained),
                    ItemStack.CODEC.listOf().fieldOf("items").forGetter(HotpotPaperBowlDataComponent::items),
                    ItemStack.CODEC.listOf().fieldOf("skewers").forGetter(HotpotPaperBowlDataComponent::skewers)
            ).apply(data, HotpotPaperBowlDataComponent::new))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotPaperBowlDataComponent> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
            StreamCodec.composite(
                    HotpotSoupTypeSerializers.getStreamHolderCodec(), HotpotPaperBowlDataComponent::soupTypeHolder,
                    ByteBufCodecs.BOOL, HotpotPaperBowlDataComponent::drained,
                    ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC), HotpotPaperBowlDataComponent::items,
                    ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC), HotpotPaperBowlDataComponent::skewers,
                    HotpotPaperBowlDataComponent::new
            )
    );

    public HotpotPaperBowlDataComponent setSoupType(IHotpotSoup soup) {
        return new HotpotPaperBowlDataComponent(soup.getSoupTypeHolder(), drained, List.copyOf(items), List.copyOf(skewers));
    }

    public HotpotPaperBowlDataComponent setDrained(boolean drained) {
        return new HotpotPaperBowlDataComponent(soupTypeHolder, drained, List.copyOf(items), List.copyOf(skewers));
    }

    public HotpotPaperBowlDataComponent setItems(List<ItemStack> items) {
        return new HotpotPaperBowlDataComponent(soupTypeHolder, drained, List.copyOf(items), List.copyOf(skewers));
    }

    public HotpotPaperBowlDataComponent setSkewers(List<ItemStack> skewers) {
        return new HotpotPaperBowlDataComponent(soupTypeHolder, drained, List.copyOf(items), List.copyOf(skewers));
    }

    public boolean isPaperBowlEmpty() {
        return items.isEmpty() && skewers.isEmpty();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotPaperBowlDataComponent data && soupTypeHolder.equals(data.soupTypeHolder) && drained == data.drained && ItemStack.listMatches(items, data.items) && ItemStack.listMatches(skewers, data.skewers);
    }
}
