package com.github.argon4w.hotpot.client.placements;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.placements.renderers.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HotpotPlacementRenderers {
    public static final ResourceLocation EMPTY_PLACEMENT_RENDERER_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_placement");

    public static final ResourceKey<Registry<IHotpotPlacementRenderer>> PLACEMENT_RENDERER_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "placement_renderer"));
    public static final DeferredRegister<IHotpotPlacementRenderer> PLACEMENT_RENDERERS = DeferredRegister.create(PLACEMENT_RENDERER_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotPlacementRenderer> PLACEMENT_RENDERER_REGISTRY = PLACEMENT_RENDERERS.makeRegistry(builder -> builder.defaultKey(EMPTY_PLACEMENT_RENDERER_LOCATION));

    public static final DeferredHolder<IHotpotPlacementRenderer, HotpotSmallPlateRenderer> SMALL_PLATE_RENDERER = PLACEMENT_RENDERERS.register("small_plate", HotpotSmallPlateRenderer::new);
    public static final DeferredHolder<IHotpotPlacementRenderer, HotpotLongPlateRenderer> LONG_PLATE_RENDERER = PLACEMENT_RENDERERS.register("long_plate", HotpotLongPlateRenderer::new);
    public static final DeferredHolder<IHotpotPlacementRenderer, HotpotLargeRoundPlateRenderer> LARGE_ROUND_PLATE_RENDERER = PLACEMENT_RENDERERS.register("large_round_plate", HotpotLargeRoundPlateRenderer::new);
    public static final DeferredHolder<IHotpotPlacementRenderer, HotpotPlacedChopstickRenderer> PLACED_CHOPSTICK_RENDERER = PLACEMENT_RENDERERS.register("placed_chopstick", HotpotPlacedChopstickRenderer::new);
    public static final DeferredHolder<IHotpotPlacementRenderer, HotpotPlacedSpoonRenderer> PLACED_SPOON_RENDERER = PLACEMENT_RENDERERS.register("placed_spoon", HotpotPlacedSpoonRenderer::new);
    public static final DeferredHolder<IHotpotPlacementRenderer, HotpotPlacedPaperBowlRenderer> PLACED_PAPER_BOWL_RENDERER = PLACEMENT_RENDERERS.register("placed_paper_bowl", HotpotPlacedPaperBowlRenderer::new);
    public static final DeferredHolder<IHotpotPlacementRenderer, HotpotEmptyPlacementRenderer> EMPTY_PLACEMENT_RENDERER = PLACEMENT_RENDERERS.register("empty_placement", HotpotEmptyPlacementRenderer::new);

    public static IHotpotPlacementRenderer getEmptyPlacementRenderer() {
        return EMPTY_PLACEMENT_RENDERER.get();
    }

    public static Registry<IHotpotPlacementRenderer> getPlacementRendererRegistry() {
        return PLACEMENT_RENDERER_REGISTRY;
    }

    public static IHotpotPlacementRenderer getPlacementRenderer(ResourceLocation resourceLocation) {
        return getPlacementRendererRegistry().get(resourceLocation);
    }
}
