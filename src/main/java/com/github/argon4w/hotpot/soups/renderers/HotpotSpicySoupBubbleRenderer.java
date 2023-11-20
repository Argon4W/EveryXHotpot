package com.github.argon4w.hotpot.soups.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;

public class HotpotSpicySoupBubbleRenderer implements IHotpotSoupCustomElementRenderer {
    private final HotpotBubbleRenderer largeBubbleRenderer, smallBubbleRenderer;

    public HotpotSpicySoupBubbleRenderer() {
        largeBubbleRenderer = new HotpotBubbleRenderer(0.21f, 0.8f, 35, new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_spicy_soup_bubble_large"));
        smallBubbleRenderer = new HotpotBubbleRenderer(0.35f, 0.55f, 45, new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_spicy_soup_bubble_small"));
    }

    @Override
    public void render(TileEntityRendererDispatcher context, HotpotBlockEntity blockEntity, float partialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay, float renderedWaterLevel) {
        largeBubbleRenderer.render(context, blockEntity, partialTick, poseStack, bufferSource, combinedLight, combinedLight, renderedWaterLevel);
        smallBubbleRenderer.render(context, blockEntity, partialTick, poseStack, bufferSource, combinedLight, combinedLight, renderedWaterLevel);
    }
}
