package com.github.argon4w.hotpot.client.items.process;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface IHotpotSpriteProcessor {
    void processSpriteImage(NativeImage original, NativeImage image, FrameSize frameSize, int frame);
    String getProcessedSuffix();
    ResourceLocation getResourceLocation();
    int getIndex();
    int processColor(ItemStack itemStack);
}
