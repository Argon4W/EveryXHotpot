package com.github.argon4w.hotpot.events;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntityRenderer;
import com.github.argon4w.hotpot.blocks.HotpotPlateBlockEntityRenderer;
import com.github.argon4w.hotpot.items.HotpotBlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = HotpotModEntry.MODID, value = Dist.CLIENT)
public class HotpotClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        HotpotModEntry.HOTPOT_BEWLR = new HotpotBlockEntityWithoutLevelRenderer();

        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(HotpotModEntry.HOTPOT_BLOCK.get(), RenderType.cutout());
        });
    }

    @SubscribeEvent
    public static void onRegisterAdditional(ModelRegistryEvent event) {
        ForgeModelBakery.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_clear_soup_bubble"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_clear_soup"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_spicy_soup_bubble_small"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_spicy_soup_bubble_large"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_spicy_soup_floating_pepper_1"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_spicy_soup_floating_pepper_2"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_spicy_soup"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_cheese_soup_bubble"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_cheese_soup"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_lava_soup"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "item/hotpot_chopstick_model"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "block/hotpot_plate_long"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "block/hotpot_plate_small"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "block/hotpot_chopstick_stand"));
        ForgeModelBakery.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "item/hotpot_spice_pack_model"));
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(HotpotModEntry.HOTPOT_BLOCK_ENTITY.get(), HotpotBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(HotpotModEntry.HOTPOT_PLACEABLE_BLOCK_ENTITY.get(), HotpotPlateBlockEntityRenderer::new);
    }
}
