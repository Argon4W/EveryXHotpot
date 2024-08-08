package com.github.argon4w.hotpot.client.soups;

import com.github.argon4w.hotpot.client.HotpotColor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public record HotpotSoupRendererConfig(Optional<ResourceLocation> soupModelResourceLocation, boolean fixedLighting, List<IHotpotSoupCustomElementRenderer> customElementRenderers, Optional<HotpotColor> color) {
    public static final Codec<HotpotSoupRendererConfig> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(config -> config.group(
                    ResourceLocation.CODEC.optionalFieldOf("soup_model_resource_location").forGetter(HotpotSoupRendererConfig::soupModelResourceLocation),
                    Codec.BOOL.optionalFieldOf("fixed_lighting", false).forGetter(HotpotSoupRendererConfig::fixedLighting),
                    HotpotSoupCustomElements.CODEC.listOf().optionalFieldOf("custom_elements_renderers", List.of()).forGetter(HotpotSoupRendererConfig::customElementRenderers),
                    HotpotColor.CODEC.optionalFieldOf("color").forGetter(HotpotSoupRendererConfig::color)
            ).apply(config, HotpotSoupRendererConfig::new))
    );
}