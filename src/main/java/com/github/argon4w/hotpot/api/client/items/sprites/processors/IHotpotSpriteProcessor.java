package com.github.argon4w.hotpot.api.client.items.sprites.processors;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.resources.metadata.animation.FrameSize;

public interface IHotpotSpriteProcessor {
    void processSpriteImage(NativeImage original, NativeImage image, FrameSize frameSize, int frame);
    String getSuffix();
}
