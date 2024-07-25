package com.github.argon4w.hotpot.client.contents;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.contents.renderers.HotpotEmptyContentRenderer;
import com.github.argon4w.hotpot.client.contents.renderers.HotpotItemContentRenderer;
import com.github.argon4w.hotpot.client.contents.renderers.HotpotPlayerContentRenderer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class HotpotContentRenderers {
    public static final ResourceLocation EMPTY_CONTENT_RENDERER_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_content");

    public static final ResourceKey<Registry<IHotpotContentRenderer>> CONTENT_RENDERER_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "content_renderer"));
    public static final DeferredRegister<IHotpotContentRenderer> CONTENT_RENDERERS = DeferredRegister.create(CONTENT_RENDERER_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotContentRenderer> CONTENT_RENDERER_REGISTRY = CONTENT_RENDERERS.makeRegistry(builder -> builder.defaultKey(EMPTY_CONTENT_RENDERER_LOCATION));

    public static final DeferredHolder<IHotpotContentRenderer, IHotpotContentRenderer> COOKING_RECIPE_CONTENT_RENDERER = CONTENT_RENDERERS.register("cooking_recipe_content", HotpotItemContentRenderer::new);
    public static final DeferredHolder<IHotpotContentRenderer, IHotpotContentRenderer> SMELTING_RECIPE_CONTENT_RENDERER = CONTENT_RENDERERS.register("smelting_recipe_content", HotpotItemContentRenderer::new);
    public static final DeferredHolder<IHotpotContentRenderer, HotpotPlayerContentRenderer> PLAYER_CONTENT_RENDERER = CONTENT_RENDERERS.register("player_content", HotpotPlayerContentRenderer::new);
    public static final DeferredHolder<IHotpotContentRenderer, HotpotEmptyContentRenderer> EMPTY_CONTENT_RENDERER = CONTENT_RENDERERS.register("empty_content", HotpotEmptyContentRenderer::new);

    public static IHotpotContentRenderer getEmptyContentRenderer() {
        return EMPTY_CONTENT_RENDERER.get();
    }

    public static Registry<IHotpotContentRenderer> getContentRendererRegistry() {
        return CONTENT_RENDERER_REGISTRY;
    }

    public static IHotpotContentRenderer getContentRenderer(ResourceLocation resourceLocation) {
        return getContentRendererRegistry().get(resourceLocation);
    }
}
