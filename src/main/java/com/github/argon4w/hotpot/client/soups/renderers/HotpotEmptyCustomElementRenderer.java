package com.github.argon4w.hotpot.client.soups.renderers;

import com.github.argon4w.hotpot.client.soups.HotpotSoupCustomElements;
import com.github.argon4w.hotpot.client.soups.IHotpotSoupCustomElementRenderer;
import com.github.argon4w.hotpot.client.soups.IHotpotSoupCustomElementRendererSerializer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class HotpotEmptyCustomElementRenderer implements IHotpotSoupCustomElementRenderer {
    @Override
    public void render(BlockEntityRendererProvider.Context context, int time, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float renderedWaterLevel) {

    }

    @Override
    public boolean shouldRenderInBowl() {
        return false;
    }

    @Override
    public IHotpotSoupCustomElementRendererSerializer<?> getSerializer() {
        return HotpotSoupCustomElements.getEmptyCustomElementRendererSerializer();
    }

    public static class Serializer implements IHotpotSoupCustomElementRendererSerializer<HotpotEmptyCustomElementRenderer> {
        public static final MapCodec<HotpotEmptyCustomElementRenderer> CODEC = MapCodec.unit(HotpotEmptyCustomElementRenderer::new);

        @Override
        public MapCodec<HotpotEmptyCustomElementRenderer> getCodec() {
            return CODEC;
        }

        public HotpotEmptyCustomElementRenderer build() {
            return new HotpotEmptyCustomElementRenderer();
        }
    }
}
