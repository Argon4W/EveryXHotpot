package com.github.argon4w.hotpot.events;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.network.HotpotUpdateSoupFactoriesPacket;
import com.github.argon4w.hotpot.soups.HotpotSoupFactoryManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = HotpotModEntry.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HotpotCommonModEvents {
    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER = new HotpotSoupFactoryManager(event.getConditionContext()));
    }

    @SubscribeEvent
    public static void onDataPackSync(OnDatapackSyncEvent event) {
        HotpotModEntry.HOTPOT_NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(event::getPlayer), new HotpotUpdateSoupFactoriesPacket(HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER.getAllFactories()));
    }
}
