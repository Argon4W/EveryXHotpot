package com.github.argon4w.hotpot.client.contents;

import com.github.argon4w.hotpot.contents.AbstractHotpotItemStackContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public interface IHotpotItemContentSpecialRenderer {
    void render(AbstractHotpotItemStackContent itemStackContent, BlockEntityRendererProvider.Context context, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float waterLevel, float rotation, float x, float z);
}
