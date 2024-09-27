package com.github.argon4w.hotpot.items.components;

import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.HotpotComponentSoupType;
import com.github.argon4w.hotpot.soups.HotpotSoupStatus;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.ArrayList;
import java.util.List;

public record HotpotPaperBowlDataComponent(ResourceKey<HotpotComponentSoupType> soupTypeKey, HotpotSoupStatus soupStatus, List<ItemStack> items, List<ItemStack> skewers) {
    public static final HotpotPaperBowlDataComponent EMPTY = new HotpotPaperBowlDataComponent(HotpotComponentSoupType.EMPTY_SOUP_TYPE_KEY, HotpotSoupStatus.FILLED, List.of(), List.of());

    public static final Codec<HotpotPaperBowlDataComponent> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                    HotpotComponentSoupType.KEY_CODEC.fieldOf("soup_type").forGetter(HotpotPaperBowlDataComponent::soupTypeKey),
                    HotpotSoupStatus.CODEC.fieldOf("soup_drained").forGetter(HotpotPaperBowlDataComponent::soupStatus),
                    ItemStack.CODEC.listOf().fieldOf("items").forGetter(HotpotPaperBowlDataComponent::items),
                    ItemStack.CODEC.listOf().fieldOf("skewers").forGetter(HotpotPaperBowlDataComponent::skewers)
            ).apply(data, HotpotPaperBowlDataComponent::new))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotPaperBowlDataComponent> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
            StreamCodec.composite(
                    HotpotComponentSoupType.KEY_STREAM_CODEC, HotpotPaperBowlDataComponent::soupTypeKey,
                    HotpotSoupStatus.STREAM_CODEC, HotpotPaperBowlDataComponent::soupStatus,
                    ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC), HotpotPaperBowlDataComponent::items,
                    ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC), HotpotPaperBowlDataComponent::skewers,
                    HotpotPaperBowlDataComponent::new
            )
    );

    public HotpotPaperBowlDataComponent setSoupType(HotpotComponentSoup soup) {
        return new HotpotPaperBowlDataComponent(soup.soupTypeHolder().getKey(), soupStatus, List.copyOf(items), List.copyOf(skewers));
    }

    public HotpotPaperBowlDataComponent setDrained(HotpotSoupStatus soupStatus) {
        return new HotpotPaperBowlDataComponent(soupTypeKey, soupStatus, List.copyOf(items), List.copyOf(skewers));
    }

    public HotpotPaperBowlDataComponent setItems(List<ItemStack> items) {
        return new HotpotPaperBowlDataComponent(soupTypeKey, soupStatus, List.copyOf(items), List.copyOf(skewers));
    }

    public HotpotPaperBowlDataComponent setSkewers(List<ItemStack> skewers) {
        return new HotpotPaperBowlDataComponent(soupTypeKey, soupStatus, List.copyOf(items), List.copyOf(skewers));
    }

    public boolean isPaperBowlEmpty() {
        return items.isEmpty() && skewers.isEmpty();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotPaperBowlDataComponent data && soupTypeKey.equals(data.soupTypeKey) && soupStatus == data.soupStatus && ItemStack.listMatches(items, data.items) && ItemStack.listMatches(skewers, data.skewers);
    }

}
