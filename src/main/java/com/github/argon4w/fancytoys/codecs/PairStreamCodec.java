package com.github.argon4w.fancytoys.codecs;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class PairStreamCodec<B, T1, T2> implements StreamCodec<B, Pair<T1, T2>> {

    private final StreamCodec<B, T1> streamCodec1;
    private final StreamCodec<B, T2> streamCodec2;

    public PairStreamCodec(StreamCodec<B, T1> streamCodec1, StreamCodec<B, T2> streamCodec2) {
        this.streamCodec1 = streamCodec1;
        this.streamCodec2 = streamCodec2;
    }

    @NotNull @Override
    public Pair<T1, T2> decode(@NotNull B buffer) {
        T1 t1 = streamCodec1.decode(buffer);
        T2 t2 = streamCodec2.decode(buffer);

        return Pair.of(t1, t2);
    }

    @Override
    public void encode(@NotNull B buffer, @NotNull Pair<T1, T2> value) {
        streamCodec1.encode(buffer, value.getFirst());
        streamCodec2.encode(buffer, value.getSecond());
    }
}
