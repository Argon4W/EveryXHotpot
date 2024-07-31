package com.github.argon4w.hotpot.client.soups.renderers;

import com.github.argon4w.hotpot.client.soups.HotpotSoupCustomElements;
import com.github.argon4w.hotpot.client.soups.IHotpotSoupCustomElementRenderer;
import com.github.argon4w.hotpot.client.soups.IHotpotSoupCustomElementRendererSerializer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.data.ModelData;

public class HotpotBubbleRenderer implements IHotpotSoupCustomElementRenderer {
    public static final RandomSource RANDOM_SOURCE = RandomSource.createNewThreadLocalInstance();
    public static final int BUBBLE_EMERGE_OFFSET_RANGE = 5;
    public static final float BUBBLE_GROWTH_TIME = 10f;
    public static final float BUBBLE_START_Y = 0.5f;
    public static final float BUBBLE_GROWTH_Y = 0.525f;

    private final Bubble[] bubbles;
    private final float spread, maxScale;
    private final ResourceLocation bubbleModelResourceLocation;

    public HotpotBubbleRenderer(float spread, float maxScale, int amount, ResourceLocation bubbleModelResourceLocation) {
        this.spread = spread;
        this.maxScale = maxScale;
        this.bubbles = new Bubble[amount];
        this.bubbleModelResourceLocation = bubbleModelResourceLocation;
    }

    @Override
    public void render(BlockEntityRendererProvider.Context context, int time, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float renderedWaterLevel) {
        BakedModel model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(ModelResourceLocation.standalone(bubbleModelResourceLocation));

        for (int i = 0; i < bubbles.length; i++) {
            Bubble bubble = bubbles[i];

            if (bubble == null || time >= bubble.startTime + bubble.offset + BUBBLE_GROWTH_TIME) {
                bubbles[i] = new Bubble(0.5f + (RANDOM_SOURCE.nextFloat() * 2f - 1f) * spread, 0.5f + (RANDOM_SOURCE.nextFloat() * 2f - 1f) * spread, RANDOM_SOURCE.nextInt(-BUBBLE_EMERGE_OFFSET_RANGE, BUBBLE_EMERGE_OFFSET_RANGE + 1), time);
                continue;
            }

            renderBubble(context, time, poseStack, bufferSource, combinedLight, combinedOverlay, bubble, model, renderedWaterLevel);
        }
    }

    public void renderBubble(BlockEntityRendererProvider.Context context, int time, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, Bubble bubble, BakedModel model, float renderedWaterLevel) {
        poseStack.pushPose();

        float progress = (time + bubble.offset) % BUBBLE_GROWTH_TIME / BUBBLE_GROWTH_TIME;
        float scale = progress * this.maxScale;
        float y = BUBBLE_START_Y + renderedWaterLevel * progress * BUBBLE_GROWTH_Y;

        poseStack.translate(bubble.x, y, bubble.z);
        poseStack.scale(scale, scale, scale);

        context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.translucentCullBlockSheet()), null, model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, RenderType.translucent());

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderInBowl() {
        return false;
    }

    @Override
    public IHotpotSoupCustomElementRendererSerializer<?> getSerializer() {
        return HotpotSoupCustomElements.HOTPOT_BUBBLE_RENDERER_SERIALIZER.get();
    }

    public float getMaxScale() {
        return maxScale;
    }

    public float getSpread() {
        return spread;
    }

    public int getAmount() {
        return bubbles.length;
    }

    public ResourceLocation getBubbleModelResourceLocation() {
        return bubbleModelResourceLocation;
    }

    public record Bubble(float x, float z, int offset, int startTime) {

    }

    public static class Serializer implements IHotpotSoupCustomElementRendererSerializer<HotpotBubbleRenderer> {
        public static final MapCodec<HotpotBubbleRenderer> CODEC = RecordCodecBuilder.mapCodec(renderer -> renderer.group(
                Codec.FLOAT.fieldOf("spread").forGetter(HotpotBubbleRenderer::getSpread),
                Codec.FLOAT.fieldOf("max_scale").forGetter(HotpotBubbleRenderer::getMaxScale),
                Codec.INT.fieldOf("amount").forGetter(HotpotBubbleRenderer::getAmount),
                ResourceLocation.CODEC.fieldOf("bubble_model_resource_location").forGetter(HotpotBubbleRenderer::getBubbleModelResourceLocation)
        ).apply(renderer, HotpotBubbleRenderer::new));

        @Override
        public MapCodec<HotpotBubbleRenderer> getCodec() {
            return CODEC;
        }
    }
}
