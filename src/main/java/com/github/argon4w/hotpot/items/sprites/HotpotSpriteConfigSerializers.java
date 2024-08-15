package com.github.argon4w.hotpot.items.sprites;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HotpotSpriteConfigSerializers {
    public static final ResourceKey<Registry<IHotpotSpriteConfigSerializer<?>>> SPRITE_CONFIG_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "sprite_config_serializer"));

    public static final Codec<IHotpotSpriteConfig> CODEC = Codec.lazyInitialized(() -> getSpriteConfigSerializerRegistry().holderByNameCodec().dispatch(IHotpotSpriteConfig::getSerializerHolder, holder -> holder.value().getCodec()));
    public static final StreamCodec<RegistryFriendlyByteBuf, IHotpotSpriteConfig> STREAM_CODEC = NeoForgeStreamCodecs.lazy(() -> ByteBufCodecs.holderRegistry(SPRITE_CONFIG_REGISTRY_KEY).dispatch(IHotpotSpriteConfig::getSerializerHolder, holder -> holder.value().getStreamCodec()));

    public static final ResourceLocation EMPTY_SPRITE_CONFIG_LOCATION = ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "empty_sprite_config");
    public static final DeferredRegister<IHotpotSpriteConfigSerializer<?>> SPRITE_CONFIGS = DeferredRegister.create(SPRITE_CONFIG_REGISTRY_KEY, HotpotModEntry.MODID);
    public static final Registry<IHotpotSpriteConfigSerializer<?>> SPRITE_CONFIG_REGISTRY = SPRITE_CONFIGS.makeRegistry(builder -> builder.defaultKey(EMPTY_SPRITE_CONFIG_LOCATION).sync(true));

    public static final DeferredHolder<IHotpotSpriteConfigSerializer<?>, HotpotSoupRendererConfigSpriteConfig.Serializer> SOUP_RENDERER_CONFIG_SPRITE_CONFIG_SERIALIZER = SPRITE_CONFIGS.register("soup_renderer_config_sprite_config", HotpotSoupRendererConfigSpriteConfig.Serializer::new);
    public static final DeferredHolder<IHotpotSpriteConfigSerializer<?>, HotpotCustomColorSpriteConfig.Serializer> CUSTOM_COLOR_SPRITE_CONFIG_SERIALIZER = SPRITE_CONFIGS.register("custom_color_sprite_config", HotpotCustomColorSpriteConfig.Serializer::new);
    public static final DeferredHolder<IHotpotSpriteConfigSerializer<?>, HotpotEmptySpriteConfig.Serializer> EMPTY_SPRITE_CONFIG_SERIALIZER = SPRITE_CONFIGS.register("empty_sprite_config", HotpotEmptySpriteConfig.Serializer::new);

    public static Registry<IHotpotSpriteConfigSerializer<?>> getSpriteConfigSerializerRegistry() {
        return SPRITE_CONFIG_REGISTRY;
    }
}
