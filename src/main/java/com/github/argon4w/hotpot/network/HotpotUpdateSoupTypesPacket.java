package com.github.argon4w.hotpot.network;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeManager;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.HashMap;

public record HotpotUpdateSoupTypesPacket(HashMap<ResourceLocation, IHotpotSoupType<?>> soupTypes) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<HotpotUpdateSoupTypesPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "update_soup_types"));
    public static final StreamCodec<RegistryFriendlyByteBuf, HotpotUpdateSoupTypesPacket> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, HotpotSoupTypeManager.STREAM_CODEC).map(HotpotUpdateSoupTypesPacket::new, HotpotUpdateSoupTypesPacket::soupTypes));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
