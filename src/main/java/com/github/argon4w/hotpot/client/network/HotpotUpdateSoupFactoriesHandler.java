package com.github.argon4w.hotpot.client.network;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.network.HotpotUpdateSoupFactoriesPacket;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class HotpotUpdateSoupFactoriesHandler {
    public static void handle(HotpotUpdateSoupFactoriesPacket packet, IPayloadContext context) {
        HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER.replaceFactories(packet.byName());
    }
}
