package com.github.argon4w.hotpot.events;

import com.github.argon4w.hotpot.HotpotModEntry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod.EventBusSubscriber(modid = HotpotModEntry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class HotpotCommonModEvents {
    @SubscribeEvent
    public static void onCommonSetup(final FMLCommonSetupEvent event) {

    }
}
