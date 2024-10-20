package com.github.argon4w.hotpot.client.contents;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.api.client.items.IHotpotStrainerBasketContentRenderer;
import com.github.argon4w.hotpot.client.contents.renderers.strainers.DefaultStrainerBasketContentRenderer;
import com.github.argon4w.hotpot.client.contents.renderers.strainers.SkewerStrainerBasketContentRenderer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HotpotStrainerBasketContentRenderers {
    public static final ResourceLocation DEFAULT_STRAINER_BASKET_CONTENT_RENDERER_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "default_strainer_basket_content_renderer");

    public static final ResourceKey<Registry<IHotpotStrainerBasketContentRenderer>> STRAINER_BASKET_CONTENT_RENDERER_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "strainer_basket_content_renderer"));
    public static final DeferredRegister<IHotpotStrainerBasketContentRenderer> STRAINER_BASKET_CONTENT_RENDERERS = DeferredRegister.create(STRAINER_BASKET_CONTENT_RENDERER_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotStrainerBasketContentRenderer> STRAINER_BASKET_CONTENT_RENDERERS_REGISTRY = STRAINER_BASKET_CONTENT_RENDERERS.makeRegistry(builder -> builder.defaultKey(DEFAULT_STRAINER_BASKET_CONTENT_RENDERER_LOCATION));

    public static final DeferredHolder<IHotpotStrainerBasketContentRenderer, SkewerStrainerBasketContentRenderer> SKEWER_STRAINER_BASKET_CONTENT_RENDERER = STRAINER_BASKET_CONTENT_RENDERERS.register("hotpot_skewer", SkewerStrainerBasketContentRenderer::new);
    public static final DeferredHolder<IHotpotStrainerBasketContentRenderer, DefaultStrainerBasketContentRenderer> DEFAULT_STRAINER_BASKET_CONTENT_RENDERER = STRAINER_BASKET_CONTENT_RENDERERS.register("default_strainer_basket_content_renderer", DefaultStrainerBasketContentRenderer::new);

    public static IHotpotStrainerBasketContentRenderer getDefaultStrainerBasketContentRenderer() {
        return DEFAULT_STRAINER_BASKET_CONTENT_RENDERER.get();
    }

    public static Registry<IHotpotStrainerBasketContentRenderer> getStrainerBasketContentRenderersRegistry() {
        return STRAINER_BASKET_CONTENT_RENDERERS_REGISTRY;
    }

    public static IHotpotStrainerBasketContentRenderer getStrainerBasketContentRenderer(ItemStack itemStack) {
        return getStrainerBasketContentRenderersRegistry().get(BuiltInRegistries.ITEM.getKey(itemStack.getItem()));
    }
}
