package com.github.argon4w.hotpot.network;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.soups.HotpotSoupTypes;
import com.github.argon4w.hotpot.soups.IHotpotSoupFactory;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import com.github.argon4w.hotpot.soups.IHotpotSoupTypeSerializer;
import com.google.common.collect.Maps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public record HotpotUpdateSoupFactoriesPacket(Map<ResourceLocation, IHotpotSoupFactory<?>> soups) {
    public void encoder(FriendlyByteBuf byteBuf) {
        byteBuf.writeCollection(soups.values(), this::writeSingleSoup);
    }

    private <T extends IHotpotSoupType> void writeSingleSoup(FriendlyByteBuf byteBuf, IHotpotSoupFactory<T> factory) {
        IHotpotSoupTypeSerializer<T> serializer = factory.getSerializer();
        ResourceLocation resourceLocation = factory.getResourceLocation();

        byteBuf.writeResourceLocation(resourceLocation);
        byteBuf.writeRegistryId(HotpotSoupTypes.getSoupTypeRegistry(), serializer);
        serializer.toNetwork(factory, byteBuf);
    }

    public static HotpotUpdateSoupFactoriesPacket decoder(FriendlyByteBuf byteBuf) {
        int length = byteBuf.readVarInt();
        HashMap<ResourceLocation, IHotpotSoupFactory<?>> soups = Maps.newHashMap();

        for (int i = 0; i < length; i ++) {
            ResourceLocation resourceLocation = byteBuf.readResourceLocation();
            IHotpotSoupTypeSerializer<?> serializer = byteBuf.readRegistryIdSafe(IHotpotSoupTypeSerializer.class);
            soups.computeIfAbsent(resourceLocation, location -> serializer.fromNetwork(resourceLocation, byteBuf));
        }

        return new HotpotUpdateSoupFactoriesPacket(soups);
    }

    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER.replaceFactories(soups))
        );
        ctx.get().setPacketHandled(true);
    }
}
