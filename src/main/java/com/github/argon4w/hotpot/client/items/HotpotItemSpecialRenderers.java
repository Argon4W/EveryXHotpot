package com.github.argon4w.hotpot.client.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.api.client.items.IHotpotItemSpecialRenderer;
import com.github.argon4w.hotpot.client.items.renderers.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HotpotItemSpecialRenderers {
    public static final ResourceLocation EMPTY_ITEM_SPECIAL_RENDERER_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_item_renderer");

    public static final ResourceKey<Registry<IHotpotItemSpecialRenderer>> ITEM_SPECIAL_RENDERER_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "item_special_renderer"));
    public static final DeferredRegister<IHotpotItemSpecialRenderer> ITEM_SPECIAL_RENDERERS = DeferredRegister.create(ITEM_SPECIAL_RENDERER_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotItemSpecialRenderer> ITEM_SPECIAL_RENDERER_REGISTRY = ITEM_SPECIAL_RENDERERS.makeRegistry(builder -> builder.defaultKey(EMPTY_ITEM_SPECIAL_RENDERER_LOCATION));

    public static final DeferredHolder<IHotpotItemSpecialRenderer, HotpotChopstickRenderer> CHOPSTICK_RENDERER = ITEM_SPECIAL_RENDERERS.register("hotpot_chopstick", HotpotChopstickRenderer::new);
    public static final DeferredHolder<IHotpotItemSpecialRenderer, HotpotSpicePackRenderer> SPICE_PACK_RENDERER = ITEM_SPECIAL_RENDERERS.register("hotpot_spice_pack", HotpotSpicePackRenderer::new);
    public static final DeferredHolder<IHotpotItemSpecialRenderer, HotpotPaperBowlRenderer> PAPER_BOWL_RENDERER = ITEM_SPECIAL_RENDERERS.register("hotpot_paper_bowl", HotpotPaperBowlRenderer::new);
    public static final DeferredHolder<IHotpotItemSpecialRenderer, HotpotSkewerRenderer> SKEWER_RENDERER = ITEM_SPECIAL_RENDERERS.register("hotpot_skewer", HotpotSkewerRenderer::new);
    public static final DeferredHolder<IHotpotItemSpecialRenderer, HotpotNapkinHolderItemRenderer> NAPKIN_HOLDER_RENDERER = ITEM_SPECIAL_RENDERERS.register("hotpot_napkin_holder", HotpotNapkinHolderItemRenderer::new);
    public static final DeferredHolder<IHotpotItemSpecialRenderer, HotpotEmptyItemSpecialRenderer> EMPTY_ITEM_RENDERER = ITEM_SPECIAL_RENDERERS.register("empty_item_renderer", HotpotEmptyItemSpecialRenderer::new);

    public static IHotpotItemSpecialRenderer getEmptyItemRenderer() {
        return EMPTY_ITEM_RENDERER.get();
    }

    public static Registry<IHotpotItemSpecialRenderer> getItemSpecialRendererRegistry() {
        return ITEM_SPECIAL_RENDERER_REGISTRY;
    }

    public static IHotpotItemSpecialRenderer getItemSpecialRenderer(ResourceLocation resourceLocation) {
        return getItemSpecialRendererRegistry().get(resourceLocation);
    }
}
