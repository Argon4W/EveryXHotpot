package com.github.argon4w.hotpot.client.soups.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.api.client.soups.renderers.IHotpotSoupCustomElementRenderer;
import com.github.argon4w.hotpot.api.client.soups.renderers.IHotpotSoupCustomElementRendererSerializer;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HotpotSoupCustomElementSerializers {
    public static final ResourceKey<Registry<IHotpotSoupCustomElementRendererSerializer<?>>> CUSTOM_ELEMENT_RENDERER_SERIALIZER_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "custom_element_renderer_serializer"));
    public static final Codec<IHotpotSoupCustomElementRenderer> CODEC = Codec.lazyInitialized(() -> getCustomElementRegistry().holderByNameCodec().dispatch(IHotpotSoupCustomElementRenderer::getSerializer, holder -> holder.value().getCodec()));

    public static final ResourceLocation EMPTY_CUSTOM_ELEMENT_RENDERER_SERIALIZER_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_custom_element_renderer");
    public static final DeferredRegister<IHotpotSoupCustomElementRendererSerializer<?>> CUSTOM_ELEMENT_RENDERER_SERIALIZERS = DeferredRegister.create(CUSTOM_ELEMENT_RENDERER_SERIALIZER_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotSoupCustomElementRendererSerializer<?>> CUSTOM_ELEMENT_RENDERER_SERIALIZER_REGISTRY = CUSTOM_ELEMENT_RENDERER_SERIALIZERS.makeRegistry(builder -> builder.defaultKey(EMPTY_CUSTOM_ELEMENT_RENDERER_SERIALIZER_LOCATION));

    public static final DeferredHolder<IHotpotSoupCustomElementRendererSerializer<?>, HotpotEmptyCustomElementRenderer.Serializer> EMPTY_CUSTOM_ELEMENT_RENDERER_SERIALIZER = CUSTOM_ELEMENT_RENDERER_SERIALIZERS.register("empty_custom_element_renderer", HotpotEmptyCustomElementRenderer.Serializer::new);
    public static final DeferredHolder<IHotpotSoupCustomElementRendererSerializer<?>, HotpotBubbleRenderer.Serializer> BUBBLE_RENDERER_SERIALIZER = CUSTOM_ELEMENT_RENDERER_SERIALIZERS.register("bubble_renderer", HotpotBubbleRenderer.Serializer::new);
    public static final DeferredHolder<IHotpotSoupCustomElementRendererSerializer<?>, HotpotSoupFloatingElementRenderer.Serializer> FLOATING_ELEMENT_RENDERER_SERIALIZER = CUSTOM_ELEMENT_RENDERER_SERIALIZERS.register("floating_element_renderer", HotpotSoupFloatingElementRenderer.Serializer::new);

    public static Registry<IHotpotSoupCustomElementRendererSerializer<?>> getCustomElementRegistry() {
        return CUSTOM_ELEMENT_RENDERER_SERIALIZER_REGISTRY;
    }
}
