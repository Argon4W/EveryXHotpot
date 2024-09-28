package com.github.argon4w.hotpot.client.soups.renderers;

import com.github.argon4w.hotpot.api.client.soups.renderers.IHotpotSoupCustomElementRenderer;
import com.github.argon4w.hotpot.api.client.soups.renderers.IHotpotSoupCustomElementRendererSerializer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class HotpotEmptyCustomElementRenderer implements IHotpotSoupCustomElementRenderer {
    @Override
    public void render(long time, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, double waterLevel) {

    }

    @Override
    public void prepareModel() {

    }

    @Override
    public boolean shouldRenderInBowl() {
        return false;
    }

    @Override
    public List<ResourceLocation> getRequiredModelResourceLocations() {
        return List.of();
    }

    @Override
    public Holder<IHotpotSoupCustomElementRendererSerializer<?>> getSerializer() {
        return HotpotSoupCustomElementSerializers.EMPTY_CUSTOM_ELEMENT_RENDERER_SERIALIZER;
    }

    public static class Serializer implements IHotpotSoupCustomElementRendererSerializer<HotpotEmptyCustomElementRenderer> {
        public static final MapCodec<HotpotEmptyCustomElementRenderer> CODEC = MapCodec.unit(HotpotEmptyCustomElementRenderer::new);

        @Override
        public MapCodec<HotpotEmptyCustomElementRenderer> getCodec() {
            return CODEC;
        }
    }
}
