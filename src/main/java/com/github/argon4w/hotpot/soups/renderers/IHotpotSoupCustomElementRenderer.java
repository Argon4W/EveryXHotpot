package com.github.argon4w.hotpot.soups.renderers;

import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public interface IHotpotSoupCustomElementRenderer {
    void render(TileEntityRendererDispatcher context, HotpotBlockEntity blockEntity, float partialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay, float renderedWaterLevel);
}
