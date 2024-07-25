package com.github.argon4w.hotpot.client.contents;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.contents.renderers.items.HotpotDefaultItemContentRenderer;
import com.github.argon4w.hotpot.client.contents.renderers.items.HotpotSkewerItemContentRenderer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HotpotItemContentSpecialRenderers {
    public static final ResourceLocation DEFAULT_ITEM_CONTENT_RENDERER_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "default_item_content");

    public static final ResourceKey<Registry<IHotpotItemContentSpecialRenderer>> ITEM_CONTENT_SPECIAL_RENDERER_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "item_content_special_renderer"));
    public static final DeferredRegister<IHotpotItemContentSpecialRenderer> ITEM_CONTENT_SPECIAL_RENDERERS = DeferredRegister.create(ITEM_CONTENT_SPECIAL_RENDERER_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotItemContentSpecialRenderer> ITEM_CONTENT_SPECIAL_RENDERER_REGISTRY = ITEM_CONTENT_SPECIAL_RENDERERS.makeRegistry(builder -> builder.defaultKey(DEFAULT_ITEM_CONTENT_RENDERER_LOCATION));

    public static final DeferredHolder<IHotpotItemContentSpecialRenderer, HotpotDefaultItemContentRenderer> DEFAULT_ITEM_CONTENT_RENDERER = ITEM_CONTENT_SPECIAL_RENDERERS.register("default_item_content", HotpotDefaultItemContentRenderer::new);
    public static final DeferredHolder<IHotpotItemContentSpecialRenderer, HotpotSkewerItemContentRenderer> SKEWER_ITEM_CONTENT_RENDERER = ITEM_CONTENT_SPECIAL_RENDERERS.register("hotpot_skewer", HotpotSkewerItemContentRenderer::new);

    public static IHotpotItemContentSpecialRenderer getDefaultItemContentRenderer() {
        return DEFAULT_ITEM_CONTENT_RENDERER.get();
    }

    public static Registry<IHotpotItemContentSpecialRenderer> getItemContentSpecialRendererRegistry() {
        return ITEM_CONTENT_SPECIAL_RENDERER_REGISTRY;
    }

    public static IHotpotItemContentSpecialRenderer getItemContentSpecialRenderer(ResourceLocation resourceLocation) {
        return getItemContentSpecialRendererRegistry().get(resourceLocation);
    }
}
