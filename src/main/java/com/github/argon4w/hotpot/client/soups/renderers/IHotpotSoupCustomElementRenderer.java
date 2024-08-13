package com.github.argon4w.hotpot.client.soups.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface IHotpotSoupCustomElementRenderer {
    void render(long time, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float waterLevel);
    void prepareModel();
    boolean shouldRenderInBowl();
    List<ResourceLocation> getRequiredModelResourceLocations();
    Holder<IHotpotSoupCustomElementRendererSerializer<?>> getSerializer();
}
