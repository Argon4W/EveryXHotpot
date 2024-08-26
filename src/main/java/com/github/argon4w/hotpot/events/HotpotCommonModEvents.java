package com.github.argon4w.hotpot.events;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.soups.HotpotComponentSoupType;
import com.github.argon4w.hotpot.soups.components.HotpotSoupComponentTypeSerializers;
import com.github.argon4w.hotpot.soups.recipes.effects.HotpotRandomMobEffectMap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = HotpotModEntry.MODID, bus = EventBusSubscriber.Bus.MOD)
public class HotpotCommonModEvents {
    @SubscribeEvent
    public static void onNewDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(HotpotSoupComponentTypeSerializers.SOUP_COMPONENT_TYPE_REGISTRY_KEY, HotpotSoupComponentTypeSerializers.TYPE_CODEC, HotpotSoupComponentTypeSerializers.TYPE_CODEC);
        event.dataPackRegistry(HotpotComponentSoupType.COMPONENT_SOUP_TYPE_REGISTRY_KEY, HotpotComponentSoupType.TYPE_CODEC, HotpotComponentSoupType.TYPE_CODEC);
        event.dataPackRegistry(HotpotRandomMobEffectMap.RANDOM_MOB_EFFECT_MAP_REGISTRY_KEY, HotpotRandomMobEffectMap.CODEC, HotpotRandomMobEffectMap.CODEC);
    }
}
