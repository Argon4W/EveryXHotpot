package com.github.argon4w.hotpot.client.events;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.sections.HotpotAdditionalSectionGeometryBlockEntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = HotpotModEntry.MODID, value = Dist.CLIENT)
public class HotpotClientGameEvents {
    @SubscribeEvent
    public static void addSectionGeometry(AddSectionGeometryEvent event) {
        event.addRenderer(new HotpotAdditionalSectionGeometryBlockEntityRenderers(event.getSectionOrigin().immutable()));
    }
}