package com.github.argon4w.hotpot.client.contents.renderers;

import com.github.argon4w.hotpot.client.contents.IHotpotContentRenderer;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public class HotpotEmptyContentRenderer implements IHotpotContentRenderer {
    @Override
    public void render(IHotpotContent content, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, double rotation, double waterLevel, double x, double z) {

    }
}
