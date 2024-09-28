package com.github.argon4w.hotpot.api.client.contents;

import com.github.argon4w.hotpot.api.contents.IHotpotContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public interface IHotpotContentRenderer {
    void render(IHotpotContent content, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, double rotation, double waterLevel, double x, double z);
}
