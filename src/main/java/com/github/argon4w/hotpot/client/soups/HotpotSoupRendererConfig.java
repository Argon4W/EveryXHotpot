package com.github.argon4w.hotpot.client.soups;

import com.github.argon4w.hotpot.client.HotpotColor;
import com.github.argon4w.hotpot.client.soups.effects.HotpotSoupClientTickEffects;
import com.github.argon4w.hotpot.client.soups.effects.IHotpotSoupClientTickEffect;
import com.github.argon4w.hotpot.client.soups.renderers.HotpotSoupCustomElementSerializers;
import com.github.argon4w.hotpot.client.soups.renderers.IHotpotSoupCustomElementRenderer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record HotpotSoupRendererConfig(Optional<ResourceLocation> soupModelResourceLocation, boolean fixedLighting, Optional<HotpotColor> color, List<IHotpotSoupCustomElementRenderer> customElementRenderers, List<IHotpotSoupClientTickEffect> clientTickEffects) {
    public static final Codec<HotpotSoupRendererConfig> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(config -> config.group(
                    ResourceLocation.CODEC.optionalFieldOf("soup_model_resource_location").forGetter(HotpotSoupRendererConfig::soupModelResourceLocation),
                    Codec.BOOL.optionalFieldOf("fixed_lighting", false).forGetter(HotpotSoupRendererConfig::fixedLighting),
                    HotpotColor.CODEC.optionalFieldOf("color").forGetter(HotpotSoupRendererConfig::color),
                    HotpotSoupCustomElementSerializers.CODEC.listOf().optionalFieldOf("custom_elements_renderers", List.of()).forGetter(HotpotSoupRendererConfig::customElementRenderers),
                    HotpotSoupClientTickEffects.CODEC.listOf().optionalFieldOf("client_tick_effects", List.of()).forGetter(HotpotSoupRendererConfig::clientTickEffects)
            ).apply(config, HotpotSoupRendererConfig::new))
    );

    public Stream<ResourceLocation> getRequiredModelResourceLocations() {
        return Stream.concat(customElementRenderers.stream().map(IHotpotSoupCustomElementRenderer::getRequiredModelResourceLocations).flatMap(Collection::stream), soupModelResourceLocation.stream());
    }
}