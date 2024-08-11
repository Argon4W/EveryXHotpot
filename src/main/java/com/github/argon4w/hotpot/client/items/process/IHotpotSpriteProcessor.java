package com.github.argon4w.hotpot.client.items.process;

import com.github.argon4w.hotpot.client.HotpotColor;
import com.github.argon4w.hotpot.items.process.IHotpotSpriteProcessorConfig;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.resources.metadata.animation.FrameSize;

public interface IHotpotSpriteProcessor {
    void processSpriteImage(NativeImage original, NativeImage image, FrameSize frameSize, int frame);
    String getProcessedSuffix();
    HotpotColor getColor(IHotpotSpriteProcessorConfig config);
}
