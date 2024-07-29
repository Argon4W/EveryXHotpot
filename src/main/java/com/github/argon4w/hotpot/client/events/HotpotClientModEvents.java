package com.github.argon4w.hotpot.client.events;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.blocks.HotpotBlockEntityRenderer;
import com.github.argon4w.hotpot.client.blocks.HotpotPlacementBlockEntityRenderer;
import com.github.argon4w.hotpot.client.items.HotpotBlockEntityWithoutLevelRenderer;
import com.github.argon4w.hotpot.client.items.HotpotClientItemExtensions;
import com.github.argon4w.hotpot.client.soups.HotpotSoupRendererConfigManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = HotpotModEntry.MODID, value = Dist.CLIENT)
public class HotpotClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        HotpotModEntry.HOTPOT_SPECIAL_ITEM_RENDERER = new HotpotBlockEntityWithoutLevelRenderer();
    }

    @SubscribeEvent
    public static void onRegisterClientReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(HotpotModEntry.HOTPOT_SOUP_RENDERER_CONFIG_MANAGER = new HotpotSoupRendererConfigManager());
    }

    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new HotpotClientItemExtensions(), HotpotModEntry.HOTPOT_CHOPSTICK.get());
        event.registerItem(new HotpotClientItemExtensions(), HotpotModEntry.HOTPOT_PAPER_BOWL.get());
        event.registerItem(new HotpotClientItemExtensions(), HotpotModEntry.HOTPOT_SKEWER.get());
        event.registerItem(new HotpotClientItemExtensions(), HotpotModEntry.HOTPOT_SPICE_PACK.get());
    }

    @SubscribeEvent
    public static void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
        event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "item/hotpot_chopstick_model")));
        event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "block/hotpot_plate_long")));
        event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "block/hotpot_plate_small")));
        event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "block/hotpot_plate_large_round")));
        event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "block/hotpot_chopstick_stand")));
        event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "item/hotpot_spice_pack_model")));
        event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "item/hotpot_paper_bowl_model")));
        event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "item/hotpot_skewer_model")));
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(HotpotModEntry.HOTPOT_BLOCK_ENTITY.get(), HotpotBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(HotpotModEntry.HOTPOT_PLACEMENT_BLOCK_ENTITY.get(), HotpotPlacementBlockEntityRenderer::new);
    }
}
