package com.github.argon4w.hotpot;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = HotpotModEntry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class HotpotCommonModEvents {
    @SubscribeEvent
    public static void onCommonSetup(final FMLCommonSetupEvent event) {
        HotpotModEntry.NETWORK_CHANNEL.registerMessage(0, HotpotBlockEntitySyncItemMessage.class,
                HotpotBlockEntitySyncItemMessage::encode,
                HotpotBlockEntitySyncItemMessage::decode,
                HotpotBlockEntitySyncItemMessage::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(HotpotModEntry.HOTPOT_BLOCK_ENTITY.get(), HotpotBlockEntityRenderer::new);
    }
}
