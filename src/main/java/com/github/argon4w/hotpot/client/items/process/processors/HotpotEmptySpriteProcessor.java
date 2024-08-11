package com.github.argon4w.hotpot.client.items.process.processors;

import com.github.argon4w.hotpot.client.HotpotColor;
import com.github.argon4w.hotpot.client.items.process.IHotpotSpriteProcessor;
import com.github.argon4w.hotpot.items.process.IHotpotSpriteProcessorConfig;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.resources.metadata.animation.FrameSize;

public class HotpotEmptySpriteProcessor implements IHotpotSpriteProcessor {
    @Override
    public void processSpriteImage(NativeImage original, NativeImage image, FrameSize frameSize, int frame) {

    }

    @Override
    public String getProcessedSuffix() {
        return "_empty_sprite_processor";
    }

    @Override
    public HotpotColor getColor(IHotpotSpriteProcessorConfig config) {
        return HotpotColor.WHITE;
    }
}
