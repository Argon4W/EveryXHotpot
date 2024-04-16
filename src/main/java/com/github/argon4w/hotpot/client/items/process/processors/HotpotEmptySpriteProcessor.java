package com.github.argon4w.hotpot.client.items.process.processors;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.items.process.IHotpotSpriteProcessor;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class HotpotEmptySpriteProcessor implements IHotpotSpriteProcessor {
    @Override
    public void processSpriteImage(NativeImage original, NativeImage image, FrameSize frameSize, int frame) {

    }

    @Override
    public String getProcessedSuffix() {
        return "_empty_sprite_processor";
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(HotpotModEntry.MODID, "empty_sprite_processor");
    }

    @Override
    public int getIndex() {
        return -1;
    }

    @Override
    public int processColor(ItemStack itemStack) {
        return -1;
    }
}
