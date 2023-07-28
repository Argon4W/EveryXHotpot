package com.github.argon4w.hotpot.soups.renderers;

import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public interface IHotpotSoupCustomElementRenderer {
    void render(BlockEntityRendererProvider.Context context, HotpotBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float renderedWaterLevel);
}
