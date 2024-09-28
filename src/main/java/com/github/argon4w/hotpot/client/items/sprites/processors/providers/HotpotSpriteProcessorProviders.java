package com.github.argon4w.hotpot.client.items.sprites.processors.providers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.api.client.items.sprites.processors.providers.IHotpotSpriteProcessorProvider;
import com.github.argon4w.hotpot.api.items.sprites.IHotpotSpriteConfig;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HotpotSpriteProcessorProviders {
    public static final ResourceKey<Registry<IHotpotSpriteProcessorProvider>> SPRITE_PROCESSOR_PROVIDER_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "sprite_processor_provider"));

    public static final ResourceLocation EMPTY_SPRITE_PROCESSOR_PROVIDER_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_sprite_processor_provider");
    public static final DeferredRegister<IHotpotSpriteProcessorProvider> SPRITE_PROCESSOR_PROVIDERS = DeferredRegister.create(SPRITE_PROCESSOR_PROVIDER_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotSpriteProcessorProvider> SPRITE_PROCESSOR_PROVIDER_REGISTRY = SPRITE_PROCESSOR_PROVIDERS.makeRegistry(builder -> builder.defaultKey(EMPTY_SPRITE_PROCESSOR_PROVIDER_LOCATION));

    public static final DeferredHolder<IHotpotSpriteProcessorProvider, HotpotSoupRendererConfigSpriteProcessorProvider> SOUP_RENDERER_CONFIG_SPRITE_PROCESSOR_PROVIDER = SPRITE_PROCESSOR_PROVIDERS.register("soup_renderer_config_sprite_config", HotpotSoupRendererConfigSpriteProcessorProvider::new);
    public static final DeferredHolder<IHotpotSpriteProcessorProvider, HotpotCustomColorSpriteProcessorProvider> CUSTOM_COLOR_SPRITE_PROCESSOR_PROVIDER = SPRITE_PROCESSOR_PROVIDERS.register("custom_color_sprite_config", HotpotCustomColorSpriteProcessorProvider::new);
    public static final DeferredHolder<IHotpotSpriteProcessorProvider, HotpotEmptySpriteProcessorProvider> EMPTY_SPRITE_PROCESSOR_PROVIDER = SPRITE_PROCESSOR_PROVIDERS.register("empty_sprite_processor_provider", HotpotEmptySpriteProcessorProvider::new);

    public static HotpotEmptySpriteProcessorProvider getEmptySpriteProcessorProvider() {
        return EMPTY_SPRITE_PROCESSOR_PROVIDER.get();
    }

    public static Registry<IHotpotSpriteProcessorProvider> getSpriteProcessorProviderRegistry() {
        return SPRITE_PROCESSOR_PROVIDER_REGISTRY;
    }

    public static IHotpotSpriteProcessorProvider getSpriteProcessorProvider(IHotpotSpriteConfig config) {
        return config.getSerializerHolder().unwrapKey().map(ResourceKey::location).map(HotpotSpriteProcessorProviders::getSpriteProcessorProvider).orElse(getEmptySpriteProcessorProvider());
    }

    public static IHotpotSpriteProcessorProvider getSpriteProcessorProvider(ResourceLocation resourceLocation) {
        return getSpriteProcessorProviderRegistry().get(resourceLocation);
    }

    public static ResourceLocation getProcessorResourceLocation(IHotpotSpriteConfig config) {
        return getSpriteProcessorProvider(config).getProcessorResourceLocation(config);
    }
}
