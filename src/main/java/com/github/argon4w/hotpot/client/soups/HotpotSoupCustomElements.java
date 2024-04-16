package com.github.argon4w.hotpot.client.soups;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.soups.renderers.HotpotBubbleRenderer;
import com.github.argon4w.hotpot.client.soups.renderers.HotpotEmptyCustomElementRenderer;
import com.github.argon4w.hotpot.client.soups.renderers.HotpotSoupFloatingElementRenderer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class HotpotSoupCustomElements {
    public static final ResourceLocation EMPTY_CUSTOM_ELEMENT_RENDERER_LOCATION = new ResourceLocation(HotpotModEntry.MODID, "empty_custom_element_renderer");
    public static final ResourceKey<Registry<IHotpotSoupCustomElementRendererSerializer<?>>> CUSTOM_ELEMENT_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(HotpotModEntry.MODID, "custom_element"));
    public static final DeferredRegister<IHotpotSoupCustomElementRendererSerializer<?>> CUSTOM_ELEMENTS = DeferredRegister.create(CUSTOM_ELEMENT_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Supplier<IForgeRegistry<IHotpotSoupCustomElementRendererSerializer<?>>> CUSTOM_ELEMENT_REGISTRY = CUSTOM_ELEMENTS.makeRegistry(() -> new RegistryBuilder<IHotpotSoupCustomElementRendererSerializer<?>>().setDefaultKey(EMPTY_CUSTOM_ELEMENT_RENDERER_LOCATION));

    public static final RegistryObject<HotpotEmptyCustomElementRenderer.Serializer> HOTPOT_EMPTY_CUSTOM_ELEMENT_RENDERER_SERIALIZER = CUSTOM_ELEMENTS.register("empty_custom_element_renderer", HotpotEmptyCustomElementRenderer.Serializer::new);
    public static final RegistryObject<HotpotBubbleRenderer.Serializer> HOTPOT_BUBBLE_RENDERER_SERIALIZER = CUSTOM_ELEMENTS.register("bubble_renderer", HotpotBubbleRenderer.Serializer::new);
    public static final RegistryObject<HotpotSoupFloatingElementRenderer.Serializer> HOTPOT_FLOATING_ELEMENT_RENDERER_SERIALIZER = CUSTOM_ELEMENTS.register("floating_element_renderer", HotpotSoupFloatingElementRenderer.Serializer::new);

    public static IForgeRegistry<IHotpotSoupCustomElementRendererSerializer<?>> getCustomElementRegistry() {
        return CUSTOM_ELEMENT_REGISTRY.get();
    }

    public static IHotpotSoupCustomElementRendererSerializer<?> getCustomElementSerializer(ResourceLocation resourceLocation) {
        return getCustomElementRegistry().getValue(resourceLocation);
    }
}
