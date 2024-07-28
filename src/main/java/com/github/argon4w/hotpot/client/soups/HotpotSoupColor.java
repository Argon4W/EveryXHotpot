package com.github.argon4w.hotpot.client.soups;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record HotpotSoupColor(int red, int green, int blue, int alpha) {
    public static final Codec<HotpotSoupColor> CODEC = RecordCodecBuilder.create(color -> color.group(
            Codec.INT.fieldOf("red").forGetter(HotpotSoupColor::red),
            Codec.INT.fieldOf("green").forGetter(HotpotSoupColor::green),
            Codec.INT.fieldOf("blue").forGetter(HotpotSoupColor::blue),
            Codec.INT.optionalFieldOf("alpha", 255).forGetter(HotpotSoupColor::alpha)
    ).apply(color, HotpotSoupColor::new));

    public int toInt() {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
