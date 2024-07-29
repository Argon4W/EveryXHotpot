package com.github.argon4w.hotpot.items.components;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeFactoryHolder;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.github.argon4w.hotpot.soups.HotpotSoupTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public record HotpotPaperBowlDataComponent(HotpotSoupTypeFactoryHolder<?> soupTypeFactory, boolean drained, List<ItemStack> items, List<ItemStack> skewers) {
    public static final HotpotPaperBowlDataComponent EMPTY = new HotpotPaperBowlDataComponent(HotpotSoupTypes.getEmptySoupFactoryHolder(), false, List.of(), List.of());

    public static final Codec<HotpotPaperBowlDataComponent> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                    HotpotSoupTypes.getHolderCodec().fieldOf("soup_type").forGetter(HotpotPaperBowlDataComponent::soupTypeFactory),
                    Codec.BOOL.fieldOf("soup_drained").forGetter(HotpotPaperBowlDataComponent::drained),
                    ItemStack.CODEC.listOf().fieldOf("items").forGetter(HotpotPaperBowlDataComponent::items),
                    ItemStack.CODEC.listOf().fieldOf("skewers").forGetter(HotpotPaperBowlDataComponent::skewers)
            ).apply(data, HotpotPaperBowlDataComponent::new))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotPaperBowlDataComponent> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
            StreamCodec.composite(
                    HotpotSoupTypes.getStreamHolderCodec(), HotpotPaperBowlDataComponent::soupTypeFactory,
                    ByteBufCodecs.BOOL, HotpotPaperBowlDataComponent::drained,
                    ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC), HotpotPaperBowlDataComponent::items,
                    ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC), HotpotPaperBowlDataComponent::skewers,
                    HotpotPaperBowlDataComponent::new
            )
    );

    public HotpotPaperBowlDataComponent setSoupType(IHotpotSoupType soupType) {
        return new HotpotPaperBowlDataComponent(soupType.getSoupTypeFactoryHolder(), drained, List.copyOf(items), List.copyOf(skewers));
    }

    public HotpotPaperBowlDataComponent setDrained(boolean drained) {
        return new HotpotPaperBowlDataComponent(soupTypeFactory, drained, List.copyOf(items), List.copyOf(skewers));
    }

    public HotpotPaperBowlDataComponent setItems(List<ItemStack> items) {
        return new HotpotPaperBowlDataComponent(soupTypeFactory, drained, List.copyOf(items), List.copyOf(skewers));
    }

    public HotpotPaperBowlDataComponent setSkewers(List<ItemStack> skewers) {
        return new HotpotPaperBowlDataComponent(soupTypeFactory, drained, List.copyOf(items), List.copyOf(skewers));
    }

    public boolean isPaperBowlEmpty() {
        return items.isEmpty() && skewers.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HotpotPaperBowlDataComponent data && soupTypeFactory.equals(data.soupTypeFactory) && drained == data.drained && equalsItems(data) && equalsSkewers(data);
    }

    public boolean equalsItems(HotpotPaperBowlDataComponent another) {
        return items.size() == another.items.size() && IntStream.range(0, items.size()).allMatch(i -> ItemStack.isSameItemSameComponents(items.get(i), another.items.get(i)));
    }

    public boolean equalsSkewers(HotpotPaperBowlDataComponent another) {
        return skewers.size() == another.skewers.size() && IntStream.range(0, skewers.size()).allMatch(i -> ItemStack.isSameItemSameComponents(skewers.get(i), another.skewers.get(i)));
    }
}
