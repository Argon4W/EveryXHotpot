package com.github.argon4w.hotpot.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record HotpotColor(int red, int green, int blue, int alpha) {
    public static final HotpotColor WHITE = new HotpotColor(255, 255, 255, 255);

    public static final Codec<HotpotColor> CODEC = RecordCodecBuilder.create(color -> color.group(
            Codec.INT.fieldOf("red").forGetter(HotpotColor::red),
            Codec.INT.fieldOf("green").forGetter(HotpotColor::green),
            Codec.INT.fieldOf("blue").forGetter(HotpotColor::blue),
            Codec.INT.optionalFieldOf("alpha", 255).forGetter(HotpotColor::alpha)
    ).apply(color, HotpotColor::new));

    public static final StreamCodec<ByteBuf, HotpotColor> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, HotpotColor::red,
            ByteBufCodecs.INT, HotpotColor::green,
            ByteBufCodecs.INT, HotpotColor::blue,
            ByteBufCodecs.INT, HotpotColor::alpha,
            HotpotColor::new
    );

    public int toInt() {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
