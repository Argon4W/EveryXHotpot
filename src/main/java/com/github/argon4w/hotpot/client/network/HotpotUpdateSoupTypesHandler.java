package com.github.argon4w.hotpot.client.network;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.network.HotpotUpdateSoupTypesPacket;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class HotpotUpdateSoupTypesHandler {
    public static void handle(HotpotUpdateSoupTypesPacket packet, IPayloadContext context) {
        HotpotModEntry.HOTPOT_SOUP_TYPE_MANAGER.replaceSoupTypes(packet.soupTypes());
    }
}
