package com.github.argon4w.hotpot.items.components;

import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.HotpotComponentSoupType;
import com.github.argon4w.hotpot.soups.HotpotSoupStatus;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.ArrayList;
import java.util.List;

public record HotpotPaperBowlDataComponent(Holder<HotpotComponentSoupType> soupTypeHolder, HotpotSoupStatus soupStatus, List<ItemStack> items, List<ItemStack> skewers) {
    public static final HotpotPaperBowlDataComponent EMPTY = new HotpotPaperBowlDataComponent(HotpotComponentSoupType.UNIT_TYPE_HOLDER, HotpotSoupStatus.FILLED, List.of(), List.of());

    public static final Codec<HotpotPaperBowlDataComponent> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                    HotpotComponentSoupType.TYPE_HOLDER_CODEC.fieldOf("soup_type").forGetter(HotpotPaperBowlDataComponent::soupTypeHolder),
                    HotpotSoupStatus.CODEC.fieldOf("soup_drained").forGetter(HotpotPaperBowlDataComponent::soupStatus),
                    ItemStack.CODEC.listOf().fieldOf("items").forGetter(HotpotPaperBowlDataComponent::items),
                    ItemStack.CODEC.listOf().fieldOf("skewers").forGetter(HotpotPaperBowlDataComponent::skewers)
            ).apply(data, HotpotPaperBowlDataComponent::new))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotPaperBowlDataComponent> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
            StreamCodec.composite(
                    HotpotComponentSoupType.TYPE_HOLDER_STREAM_CODEC, HotpotPaperBowlDataComponent::soupTypeHolder,
                    HotpotSoupStatus.STREAM_CODEC, HotpotPaperBowlDataComponent::soupStatus,
                    ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC), HotpotPaperBowlDataComponent::items,
                    ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC), HotpotPaperBowlDataComponent::skewers,
                    HotpotPaperBowlDataComponent::new
            )
    );

    public HotpotPaperBowlDataComponent setSoupType(HotpotComponentSoup soup) {
        return new HotpotPaperBowlDataComponent(soup.soupTypeHolder(), soupStatus, List.copyOf(items), List.copyOf(skewers));
    }

    public HotpotPaperBowlDataComponent setDrained(HotpotSoupStatus soupStatus) {
        return new HotpotPaperBowlDataComponent(soupTypeHolder, soupStatus, List.copyOf(items), List.copyOf(skewers));
    }

    public HotpotPaperBowlDataComponent setItems(List<ItemStack> items) {
        return new HotpotPaperBowlDataComponent(soupTypeHolder, soupStatus, List.copyOf(items), List.copyOf(skewers));
    }

    public HotpotPaperBowlDataComponent setSkewers(List<ItemStack> skewers) {
        return new HotpotPaperBowlDataComponent(soupTypeHolder, soupStatus, List.copyOf(items), List.copyOf(skewers));
    }

    public boolean isPaperBowlEmpty() {
        return items.isEmpty() && skewers.isEmpty();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotPaperBowlDataComponent data && soupTypeHolder.equals(data.soupTypeHolder) && soupStatus == data.soupStatus && ItemStack.listMatches(items, data.items) && ItemStack.listMatches(skewers, data.skewers);
    }

}
