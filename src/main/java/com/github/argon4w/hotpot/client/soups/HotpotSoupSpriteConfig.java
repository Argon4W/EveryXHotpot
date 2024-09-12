package com.github.argon4w.hotpot.client.soups;

import com.github.argon4w.hotpot.client.HotpotColor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record HotpotSoupSpriteConfig(HotpotColor color, ResourceLocation processorResourceLocation) {
    public static final Codec<HotpotSoupSpriteConfig> CODEC = RecordCodecBuilder.create(config -> config.group(
            HotpotColor.CODEC.fieldOf("color").forGetter(HotpotSoupSpriteConfig::color),
            ResourceLocation.CODEC.fieldOf("processor_resource_location").forGetter(HotpotSoupSpriteConfig::processorResourceLocation)
    ).apply(config, HotpotSoupSpriteConfig::new));
}
