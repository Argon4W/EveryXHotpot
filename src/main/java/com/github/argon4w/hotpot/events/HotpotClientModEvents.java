package com.github.argon4w.hotpot.events;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntityRenderer;
import com.github.argon4w.hotpot.blocks.HotpotPlateBlockEntityRenderer;
import com.github.argon4w.hotpot.items.HotpotBlockEntityWithoutLevelRenderer;
import com.github.argon4w.hotpot.items.ISTERModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = HotpotModEntry.MODID, value = Dist.CLIENT)
public class HotpotClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            RenderTypeLookup.setRenderLayer(HotpotModEntry.HOTPOT_BLOCK.get(), RenderType.cutout());
            ClientRegistry.bindTileEntityRenderer(HotpotModEntry.HOTPOT_BLOCK_ENTITY.get(), HotpotBlockEntityRenderer::new);
            ClientRegistry.bindTileEntityRenderer(HotpotModEntry.HOTPOT_PLACEABLE_BLOCK_ENTITY.get(), HotpotPlateBlockEntityRenderer::new);
        });
    }

    @SubscribeEvent
    public static void onModelBaked(ModelBakeEvent event) {
        System.out.println("ModelBakeEvent Invoked!");
        enableISTER(event, new ModelResourceLocation(HotpotModEntry.HOTPOT_CHOPSTICK.get().getRegistryName(), "inventory"));
        enableISTER(event, new ModelResourceLocation(HotpotModEntry.HOTPOT_SPICE_PACK.get().getRegistryName(), "inventory"));
    }

    public static void enableISTER(ModelBakeEvent event, ResourceLocation location) {
        IBakedModel originalModel = event.getModelRegistry().get(location);
        ISTERModel ISTEREnabledModel = new ISTERModel(originalModel);
        event.getModelRegistry().put(location, ISTEREnabledModel);
    }

    @SubscribeEvent
    public static void onRegisterAdditional(ModelRegistryEvent event) {
        ModelLoader.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_clear_soup_bubble"));
        ModelLoader.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_clear_soup"));
        ModelLoader.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_spicy_soup_bubble_small"));
        ModelLoader.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_spicy_soup_bubble_large"));
        ModelLoader.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_spicy_soup_floating_pepper_1"));
        ModelLoader.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_spicy_soup_floating_pepper_2"));
        ModelLoader.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_spicy_soup"));
        ModelLoader.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_cheese_soup_bubble"));
        ModelLoader.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_cheese_soup"));
        ModelLoader.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_lava_soup"));
        ModelLoader.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "item/hotpot_chopstick_model"));
        ModelLoader.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "block/hotpot_plate_long"));
        ModelLoader.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "block/hotpot_plate_small"));
        ModelLoader.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "block/hotpot_chopstick_stand"));
        ModelLoader.addSpecialModel(new ResourceLocation(HotpotModEntry.MODID, "item/hotpot_spice_pack_model"));
    }
}
