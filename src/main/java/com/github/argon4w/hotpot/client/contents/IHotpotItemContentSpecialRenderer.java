package com.github.argon4w.hotpot.client.contents;

import com.github.argon4w.hotpot.contents.AbstractHotpotItemStackContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public interface IHotpotItemContentSpecialRenderer {
    void render(AbstractHotpotItemStackContent itemStackContent, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, double waterLevel, double rotation, double x, double z);
}
