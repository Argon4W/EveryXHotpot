package com.github.argon4w.hotpot.client.placements;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.placements.renderers.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class HotpotPlacementRenderers {
    public static final ResourceLocation EMPTY_PLACEMENT_RENDERER_LOCATION = new ResourceLocation(HotpotModEntry.MODID, "empty_placement");

    public static final ResourceKey<Registry<IHotpotPlacementRenderer>> PLACEMENT_RENDERER_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(HotpotModEntry.MODID, "placement_renderer"));
    public static final DeferredRegister<IHotpotPlacementRenderer> PLACEMENT_RENDERERS = DeferredRegister.create(PLACEMENT_RENDERER_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Supplier<IForgeRegistry<IHotpotPlacementRenderer>> PLACEMENT_RENDERER_REGISTRY = PLACEMENT_RENDERERS.makeRegistry(() -> new RegistryBuilder<IHotpotPlacementRenderer>().setDefaultKey(EMPTY_PLACEMENT_RENDERER_LOCATION));

    public static final RegistryObject<IHotpotPlacementRenderer> SMALL_PLATE_RENDERER = PLACEMENT_RENDERERS.register("small_plate", HotpotSmallPlateRenderer::new);
    public static final RegistryObject<IHotpotPlacementRenderer> LONG_PLATE_RENDERER = PLACEMENT_RENDERERS.register("long_plate", HotpotLongPlateRenderer::new);
    public static final RegistryObject<IHotpotPlacementRenderer> PLACED_CHOPSTICK_RENDERER = PLACEMENT_RENDERERS.register("placed_chopstick", HotpotPlacedChopstickRenderer::new);
    public static final RegistryObject<IHotpotPlacementRenderer> PLACED_SPOON_RENDERER = PLACEMENT_RENDERERS.register("placed_spoon", HotpotPlacedSpoonRenderer::new);
    public static final RegistryObject<IHotpotPlacementRenderer> PLACED_PAPER_BOWL_RENDERER = PLACEMENT_RENDERERS.register("placed_paper_bowl", HotpotPlacedPaperBowlRenderer::new);
    public static final RegistryObject<IHotpotPlacementRenderer> EMPTY_PLACEMENT_RENDERER = PLACEMENT_RENDERERS.register("empty_placement", HotpotEmptyPlacementRenderer::new);

    public static IHotpotPlacementRenderer getEmptyPlacementRenderer() {
        return EMPTY_PLACEMENT_RENDERER.get();
    }

    public static IForgeRegistry<IHotpotPlacementRenderer> getPlacementRendererRegistry() {
        return PLACEMENT_RENDERER_REGISTRY.get();
    }

    public static IHotpotPlacementRenderer getPlacementRenderer(ResourceLocation resourceLocation) {
        return getPlacementRendererRegistry().getValue(resourceLocation);
    }
}
