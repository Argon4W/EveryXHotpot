package com.github.argon4w.hotpot.client.items.sprites.processors;

import com.github.argon4w.hotpot.api.client.items.sprites.processors.IHotpotSpriteProcessor;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.resources.metadata.animation.FrameSize;

public class HotpotEmptySpriteProcessor implements IHotpotSpriteProcessor {
    @Override
    public void processSpriteImage(NativeImage original, NativeImage image, FrameSize frameSize, int frame) {

    }

    @Override
    public String getSuffix() {
        return "_empty_sprite_processor";
    }
}
