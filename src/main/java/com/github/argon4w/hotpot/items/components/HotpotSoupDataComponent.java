package com.github.argon4w.hotpot.items.components;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.soups.HotpotSoupTypes;
import com.github.argon4w.hotpot.soups.HotpotWrappedSoupTypeTypeFactory;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record HotpotSoupDataComponent(HotpotWrappedSoupTypeTypeFactory<?> soupTypeFactory) {
    public static final HotpotSoupDataComponent EMPTY = new HotpotSoupDataComponent(HotpotSoupTypes.getEmptySoupFactory());

    public static final Codec<HotpotSoupDataComponent> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(data -> data.group(
                    HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER.wrappedCodec.fieldOf("soup_type").forGetter(HotpotSoupDataComponent::soupTypeFactory)
            ).apply(data, HotpotSoupDataComponent::new))
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotSoupDataComponent> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() ->
            StreamCodec.composite(
                    HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER.streamWrappedCodec, HotpotSoupDataComponent::soupTypeFactory,
                    HotpotSoupDataComponent::new
            )
    );

    public static boolean hasDataComponent(ItemStack itemStack) {
        return itemStack.has(HotpotModEntry.HOTPOT_SOUP_DATA_COMPONENT);
    }

    public static HotpotSoupDataComponent getDataComponent(ItemStack itemStack) {
        return itemStack.getOrDefault(HotpotModEntry.HOTPOT_SOUP_DATA_COMPONENT, EMPTY);
    }

    public static ResourceLocation getSoupTypeResourceLocation(ItemStack itemStack) {
        return getDataComponent(itemStack).soupTypeFactory.resourceLocation();
    }

    public static void setSoup(ItemStack itemStack, IHotpotSoupType soupType) {
        itemStack.set(HotpotModEntry.HOTPOT_SOUP_DATA_COMPONENT, new HotpotSoupDataComponent(HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER.getSoupFactoryFromSoupType(soupType)));
    }
}
