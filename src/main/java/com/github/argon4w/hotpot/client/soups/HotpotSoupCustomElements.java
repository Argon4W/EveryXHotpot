package com.github.argon4w.hotpot.client.soups;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.soups.renderers.HotpotBubbleRenderer;
import com.github.argon4w.hotpot.client.soups.renderers.HotpotEmptyCustomElementRenderer;
import com.github.argon4w.hotpot.client.soups.renderers.HotpotSoupFloatingElementRenderer;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HotpotSoupCustomElements {
    public static final ResourceKey<Registry<IHotpotSoupCustomElementRendererSerializer<?>>> CUSTOM_ELEMENT_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "custom_element"));

    public static final Codec<IHotpotSoupCustomElementRenderer> CODEC = Codec.lazyInitialized(() -> getCustomElementRegistry().byNameCodec().dispatch(IHotpotSoupCustomElementRenderer::getSerializer, IHotpotSoupCustomElementRendererSerializer::getCodec));

    public static final ResourceLocation EMPTY_CUSTOM_ELEMENT_RENDERER_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_custom_element_renderer");
    public static final DeferredRegister<IHotpotSoupCustomElementRendererSerializer<?>> CUSTOM_ELEMENTS = DeferredRegister.create(CUSTOM_ELEMENT_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotSoupCustomElementRendererSerializer<?>> CUSTOM_ELEMENT_REGISTRY = CUSTOM_ELEMENTS.makeRegistry(builder -> builder.defaultKey(EMPTY_CUSTOM_ELEMENT_RENDERER_LOCATION));

    public static final DeferredHolder<IHotpotSoupCustomElementRendererSerializer<?>, HotpotEmptyCustomElementRenderer.Serializer> HOTPOT_EMPTY_CUSTOM_ELEMENT_RENDERER_SERIALIZER = CUSTOM_ELEMENTS.register("empty_custom_element_renderer", HotpotEmptyCustomElementRenderer.Serializer::new);
    public static final DeferredHolder<IHotpotSoupCustomElementRendererSerializer<?>, HotpotBubbleRenderer.Serializer> HOTPOT_BUBBLE_RENDERER_SERIALIZER = CUSTOM_ELEMENTS.register("bubble_renderer", HotpotBubbleRenderer.Serializer::new);
    public static final DeferredHolder<IHotpotSoupCustomElementRendererSerializer<?>, HotpotSoupFloatingElementRenderer.Serializer> HOTPOT_FLOATING_ELEMENT_RENDERER_SERIALIZER = CUSTOM_ELEMENTS.register("floating_element_renderer", HotpotSoupFloatingElementRenderer.Serializer::new);

    public static HotpotEmptyCustomElementRenderer buildEmptyCustomElementRenderer() {
        return getEmptyCustomElementRendererSerializer().build();
    }

    public static HotpotEmptyCustomElementRenderer.Serializer getEmptyCustomElementRendererSerializer() {
        return HOTPOT_EMPTY_CUSTOM_ELEMENT_RENDERER_SERIALIZER.get();
    }

    public static Registry<IHotpotSoupCustomElementRendererSerializer<?>> getCustomElementRegistry() {
        return CUSTOM_ELEMENT_REGISTRY;
    }

    public static IHotpotSoupCustomElementRendererSerializer<?> getCustomElementSerializer(ResourceLocation resourceLocation) {
        return getCustomElementRegistry().get(resourceLocation);
    }
}
