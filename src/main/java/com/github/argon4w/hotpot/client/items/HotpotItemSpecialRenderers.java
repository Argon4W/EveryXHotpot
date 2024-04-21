package com.github.argon4w.hotpot.client.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.items.renderers.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class HotpotItemSpecialRenderers {
    public static final ResourceLocation EMPTY_ITEM_SPECIAL_RENDERER_LOCATION = new ResourceLocation(HotpotModEntry.MODID, "empty_item_renderer");

    public static final ResourceKey<Registry<IHotpotItemSpecialRenderer>> ITEM_SPECIAL_RENDERER_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(HotpotModEntry.MODID, "item_special_renderer"));
    public static final DeferredRegister<IHotpotItemSpecialRenderer> ITEM_SPECIAL_RENDERERS = DeferredRegister.create(ITEM_SPECIAL_RENDERER_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Supplier<IForgeRegistry<IHotpotItemSpecialRenderer>> ITEM_SPECIAL_RENDERER_REGISTRY = ITEM_SPECIAL_RENDERERS.makeRegistry(() -> new RegistryBuilder<IHotpotItemSpecialRenderer>().setDefaultKey(EMPTY_ITEM_SPECIAL_RENDERER_LOCATION));

    public static final RegistryObject<IHotpotItemSpecialRenderer> CHOPSTICK_RENDERER = ITEM_SPECIAL_RENDERERS.register("hotpot_chopstick", HotpotChopstickRenderer::new);
    public static final RegistryObject<IHotpotItemSpecialRenderer> SPICE_PACK_RENDERER = ITEM_SPECIAL_RENDERERS.register("hotpot_spice_pack", HotpotSpicePackRenderer::new);
    public static final RegistryObject<IHotpotItemSpecialRenderer> PAPER_BOWL_RENDERER = ITEM_SPECIAL_RENDERERS.register("hotpot_paper_bowl", HotpotPaperBowlRenderer::new);
    public static final RegistryObject<IHotpotItemSpecialRenderer> SKEWER_RENDERER = ITEM_SPECIAL_RENDERERS.register("hotpot_skewer", HotpotSkewerRenderer::new);
    public static final RegistryObject<IHotpotItemSpecialRenderer> EMPTY_ITEM_RENDERER = ITEM_SPECIAL_RENDERERS.register("empty_item_renderer", HotpotEmptyItemSpecialRenderer::new);

    public static IHotpotItemSpecialRenderer getEmptyItemRenderer() {
        return EMPTY_ITEM_RENDERER.get();
    }

    public static IForgeRegistry<IHotpotItemSpecialRenderer> getItemSpecialRendererRegistry() {
        return ITEM_SPECIAL_RENDERER_REGISTRY.get();
    }

    public static IHotpotItemSpecialRenderer getItemSpecialRenderer(ResourceLocation resourceLocation) {
        return getItemSpecialRendererRegistry().getValue(resourceLocation);
    }
}
