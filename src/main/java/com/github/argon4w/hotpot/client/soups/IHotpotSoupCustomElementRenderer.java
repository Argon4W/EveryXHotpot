package com.github.argon4w.hotpot.client.soups;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface IHotpotSoupCustomElementRenderer {
    void render(long time, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float renderedWaterLevel);
    boolean shouldRenderInBowl();
    List<ResourceLocation> getRequiredModelResourceLocations();
    IHotpotSoupCustomElementRendererSerializer<?> getSerializer();
}
