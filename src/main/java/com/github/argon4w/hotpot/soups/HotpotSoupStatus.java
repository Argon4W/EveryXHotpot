package com.github.argon4w.hotpot.soups;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;

public enum HotpotSoupStatus implements StringRepresentable {
    FILLED("filled", ".hotpot", 40, 1.5, false),
    DRAINED("drained", ".drained", 20, 1.0, true);

    public static final Codec<HotpotSoupStatus> CODEC = StringRepresentable.fromEnum(HotpotSoupStatus::values);
    public static final StreamCodec<FriendlyByteBuf, HotpotSoupStatus> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(HotpotSoupStatus.class);

    private final String name;
    private final String suffix;
    private final int stirringSpeed;
    private final double useDurationFactor;
    private final boolean canBeOverridden;

    HotpotSoupStatus(String name, String suffix, int stirringSpeed, double useDurationFactor, boolean canBeOverridden) {
        this.name = name;
        this.suffix = suffix;
        this.stirringSpeed = stirringSpeed;
        this.useDurationFactor = useDurationFactor;
        this.canBeOverridden = canBeOverridden;
    }

    public String getSuffix() {
        return suffix;
    }

    public int getStirringSpeed() {
        return stirringSpeed;
    }

    public double getUseDurationFactor() {
        return useDurationFactor;
    }

    public boolean canBeOverridden() {
        return canBeOverridden;
    }

    @NotNull
    @Override
    public String getSerializedName() {
        return name;
    }
}
