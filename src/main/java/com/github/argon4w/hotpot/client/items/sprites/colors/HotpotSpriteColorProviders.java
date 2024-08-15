package com.github.argon4w.hotpot.client.items.sprites.colors;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.HotpotColor;
import com.github.argon4w.hotpot.items.sprites.IHotpotSpriteConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HotpotSpriteColorProviders {
    public static final ResourceKey<Registry<IHotpotSpriteColorProvider>> SPRITE_COLOR_PROVIDER_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "sprite_color_provider"));
    public static final Codec<IHotpotSpriteColorProvider> CODEC = Codec.lazyInitialized(() -> getSpriteColorProviderRegistry().byNameCodec());

    public static final ResourceLocation EMPTY_SPRITE_COLOR_PROVIDER_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_sprite_color_provider");
    public static final DeferredRegister<IHotpotSpriteColorProvider> SPRITE_COLOR_PROVIDERS = DeferredRegister.create(SPRITE_COLOR_PROVIDER_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotSpriteColorProvider> SPRITE_COLOR_PROVIDER_REGISTRY = SPRITE_COLOR_PROVIDERS.makeRegistry(builder -> builder.defaultKey(EMPTY_SPRITE_COLOR_PROVIDER_LOCATION));

    public static final DeferredHolder<IHotpotSpriteColorProvider, HotpotSoupRendererConfigSpriteColorProvider> SOUP_RENDERER_CONFIG_SPRITE_COLOR_PROVIDER = SPRITE_COLOR_PROVIDERS.register("soup_renderer_config_sprite_config", HotpotSoupRendererConfigSpriteColorProvider::new);
    public static final DeferredHolder<IHotpotSpriteColorProvider, HotpotCustomColorSpriteColorProvider> CUSTOM_COLOR_SPRITE_COLOR_CONFIG = SPRITE_COLOR_PROVIDERS.register("custom_color_sprite_config", HotpotCustomColorSpriteColorProvider::new);
    public static final DeferredHolder<IHotpotSpriteColorProvider, HotpotEmptySpriteColorProvider> EMPTY_SPRITE_COLOR_PROVIDER = SPRITE_COLOR_PROVIDERS.register("empty_sprite_color_provider", HotpotEmptySpriteColorProvider::new);

    public static HotpotEmptySpriteColorProvider getEmptySpriteColorProvider() {
        return EMPTY_SPRITE_COLOR_PROVIDER.get();
    }

    public static IHotpotSpriteColorProvider getSpriteColorProvider(IHotpotSpriteConfig config) {
        return config.getSerializerHolder().unwrapKey().map(ResourceKey::location).map(HotpotSpriteColorProviders::getSpriteColorProvider).orElse(getEmptySpriteColorProvider());
    }

    public static IHotpotSpriteColorProvider getSpriteColorProvider(ResourceLocation resourceLocation) {
        return getSpriteColorProviderRegistry().get(resourceLocation);
    }

    public static HotpotColor getColor(IHotpotSpriteConfig config) {
        return getSpriteColorProvider(config).getColor(config);
    }

    public static Registry<IHotpotSpriteColorProvider> getSpriteColorProviderRegistry() {
        return SPRITE_COLOR_PROVIDER_REGISTRY;
    }
}
