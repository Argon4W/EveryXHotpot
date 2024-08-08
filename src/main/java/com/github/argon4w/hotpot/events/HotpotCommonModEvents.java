package com.github.argon4w.hotpot.events;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.network.HotpotUpdateSoupTypesHandler;
import com.github.argon4w.hotpot.network.HotpotUpdateSoupTypesPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;

@EventBusSubscriber(modid = HotpotModEntry.MODID, bus = EventBusSubscriber.Bus.MOD)
public class HotpotCommonModEvents {
    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        event.registrar(HotpotModEntry.HOTPOT_NETWORK_PROTOCOL_VERSION).playToClient(HotpotUpdateSoupTypesPacket.TYPE, HotpotUpdateSoupTypesPacket.STREAM_CODEC, new MainThreadPayloadHandler<>(HotpotUpdateSoupTypesHandler::handle));
    }
}
