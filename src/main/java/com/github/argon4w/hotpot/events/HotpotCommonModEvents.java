package com.github.argon4w.hotpot.events;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.network.HotpotUpdateSoupFactoriesHandler;
import com.github.argon4w.hotpot.network.HotpotUpdateSoupFactoriesPacket;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeFactoryManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;

@EventBusSubscriber(modid = HotpotModEntry.MODID, bus = EventBusSubscriber.Bus.MOD)
public class HotpotCommonModEvents {
    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        event.registrar(HotpotModEntry.HOTPOT_NETWORK_PROTOCOL_VERSION).playToClient(
                HotpotUpdateSoupFactoriesPacket.TYPE,
                HotpotUpdateSoupFactoriesPacket.STREAM_CODEC,
                new MainThreadPayloadHandler<>(HotpotUpdateSoupFactoriesHandler::handle)
        );
    }
}
