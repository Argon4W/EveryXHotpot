package com.github.argon4w.hotpot.events;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.network.HotpotUpdateSoupFactoriesHandler;
import com.github.argon4w.hotpot.network.HotpotUpdateSoupFactoriesPacket;
import com.github.argon4w.hotpot.soups.HotpotSoupFactoryManager;
import com.github.argon4w.hotpot.soups.IHotpotSoupFactory;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;

import java.util.HashMap;

@EventBusSubscriber(modid = HotpotModEntry.MODID, bus = EventBusSubscriber.Bus.GAME)
public class HotpotCommonModEvents {
    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER = new HotpotSoupFactoryManager());
    }

    @SubscribeEvent
    public static void onDataPackSync(OnDatapackSyncEvent event) {
        HotpotUpdateSoupFactoriesPacket packet = new HotpotUpdateSoupFactoriesPacket(HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER.getAllFactoriesByName());

        if (event.getPlayer() == null) {
            PacketDistributor.sendToAllPlayers(packet);
        } else {
            PacketDistributor.sendToPlayer(event.getPlayer(), packet);
        }
    }

    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        event.registrar(HotpotModEntry.HOTPOT_NETWORK_PROTOCOL_VERSION).playToClient(
                HotpotUpdateSoupFactoriesPacket.TYPE,
                HotpotUpdateSoupFactoriesPacket.STREAM_CODEC,
                new MainThreadPayloadHandler<>(HotpotUpdateSoupFactoriesHandler::handle)
        );
    }
}
