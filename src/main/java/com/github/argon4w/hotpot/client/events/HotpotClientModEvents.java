package com.github.argon4w.hotpot.client.events;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.blocks.HotpotBlockEntityRenderer;
import com.github.argon4w.hotpot.client.blocks.HotpotPlacementBlockEntityRenderer;
import com.github.argon4w.hotpot.client.soups.HotpotSoupRendererConfigManager;
import com.github.argon4w.hotpot.client.items.HotpotBlockEntityWithoutLevelRenderer;
import com.github.argon4w.hotpot.soups.HotpotSoupFactoryManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = HotpotModEntry.MODID, value = Dist.CLIENT)
public class HotpotClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        HotpotModEntry.HOTPOT_SPECIAL_ITEM_RENDERER = new HotpotBlockEntityWithoutLevelRenderer();
    }

    @SubscribeEvent
    public static void onRegisterClientReloadListener(RegisterClientReloadListenersEvent event) {
        HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER = new HotpotSoupFactoryManager();
        event.registerReloadListener(HotpotModEntry.HOTPOT_SOUP_RENDERER_CONFIG_MANAGER = new HotpotSoupRendererConfigManager());
    }

    @SubscribeEvent
    public static void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
        event.register(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_clear_soup_bubble"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_clear_soup"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_spicy_soup_bubble_small"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_spicy_soup_bubble_large"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_spicy_soup_floating_pepper_1"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_spicy_soup_floating_pepper_2"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_spicy_soup"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_cheese_soup_bubble"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_cheese_soup"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_lava_soup"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_tomato_soup"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_tomato_soup_bubble"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_tomato_soup_floating_tomato_1"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_tomato_soup_floating_tomato_2"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_shimmer_soup"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "item/hotpot_chopstick_model"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "block/hotpot_plate_long"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "block/hotpot_plate_small"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "block/hotpot_plate_large_round"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "block/hotpot_chopstick_stand"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "item/hotpot_spice_pack_model"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "item/hotpot_paper_bowl_model"));
        event.register(new ResourceLocation(HotpotModEntry.MODID, "item/hotpot_skewer_model"));
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(HotpotModEntry.HOTPOT_BLOCK_ENTITY.get(), HotpotBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(HotpotModEntry.HOTPOT_PLACEMENT_BLOCK_ENTITY.get(), HotpotPlacementBlockEntityRenderer::new);
    }
}
