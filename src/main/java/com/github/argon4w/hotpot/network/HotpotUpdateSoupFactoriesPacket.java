package com.github.argon4w.hotpot.network;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.soups.HotpotSoupTypes;
import com.github.argon4w.hotpot.soups.IHotpotSoupFactorySerializer;
import com.github.argon4w.hotpot.soups.IHotpotSoupTypeFactory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.HashMap;

public record HotpotUpdateSoupFactoriesPacket(HashMap<ResourceLocation, IHotpotSoupTypeFactory<?>> byName) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<HotpotUpdateSoupFactoriesPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "update_soup_factories"));
    public static final StreamCodec<RegistryFriendlyByteBuf, IHotpotSoupTypeFactory<?>> FACTORY_STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> ByteBufCodecs.registry(HotpotSoupTypes.SOUP_REGISTRY_KEY).dispatch(IHotpotSoupTypeFactory::getSerializer, IHotpotSoupFactorySerializer::getStreamCodec));
    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotUpdateSoupFactoriesPacket> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, FACTORY_STREAM_CODEC), HotpotUpdateSoupFactoriesPacket::byName,
            HotpotUpdateSoupFactoriesPacket::new
    ));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
