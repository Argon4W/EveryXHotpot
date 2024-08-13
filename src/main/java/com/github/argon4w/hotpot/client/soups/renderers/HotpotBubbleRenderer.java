package com.github.argon4w.hotpot.client.soups.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.List;
import java.util.stream.IntStream;

public class HotpotBubbleRenderer implements IHotpotSoupCustomElementRenderer {
    private final Bubble[] bubbles;
    private final float spread;
    private final float maxScale;
    private final int offsetRange;
    private final float maxTime;
    private final float minY;
    private final float maxY;

    private final ResourceLocation bubbleModelResourceLocation;
    private final boolean shouldRenderInBowl;
    private final RandomSource randomSource;

    private BakedModel model;

    public HotpotBubbleRenderer(float spread, float maxScale, int amount, int offsetRange, float maxTime, float minY, float maxY, ResourceLocation bubbleModelResourceLocation, boolean shouldRenderInBowl) {
        this.spread = spread;
        this.maxScale = maxScale;
        this.bubbles = new Bubble[amount];
        this.offsetRange = offsetRange;
        this.maxTime = maxTime;
        this.minY = minY;
        this.maxY = maxY;

        this.bubbleModelResourceLocation = bubbleModelResourceLocation;
        this.shouldRenderInBowl = shouldRenderInBowl;
        this.randomSource = RandomSource.create();
    }

    @Override
    public void prepareModel() {
        model = Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(bubbleModelResourceLocation));
    }

    @Override
    public void render(long time, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float renderedWaterLevel) {
        IntStream.range(0, bubbles.length).forEach(i -> renderBubble(time, renderedWaterLevel, i, poseStack, bufferSource, combinedLight, combinedOverlay));
    }

    public void renderBubble(long time, float renderedWaterLevel, int bubbleIndex, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        Bubble bubble = bubbles[bubbleIndex];

        if (model == null) {
            return;
        }

        if (bubble == null || time >= bubble.time + bubble.offset + maxTime) {
            float x = 0.5f + (randomSource.nextFloat() * 2f - 1f) * spread;
            float z = 0.5f + (randomSource.nextFloat() * 2f - 1f) * spread;
            int offset = randomSource.nextInt(-offsetRange, offsetRange + 1);

            bubbles[bubbleIndex] = bubble = new Bubble(x, z, offset, time);
        }

        float progress = (time + bubble.offset) % maxTime / maxTime;
        float scale = progress * this.maxScale;
        float y = minY + renderedWaterLevel * progress * maxY;

        poseStack.pushPose();

        poseStack.translate(bubble.x, y, bubble.z);
        poseStack.scale(scale, scale, scale);

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.translucentCullBlockSheet()), null, model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, RenderType.translucent());

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderInBowl() {
        return shouldRenderInBowl;
    }

    @Override
    public List<ResourceLocation> getRequiredModelResourceLocations() {
        return List.of(bubbleModelResourceLocation);
    }

    @Override
    public Holder<IHotpotSoupCustomElementRendererSerializer<?>> getSerializer() {
        return HotpotSoupCustomElementSerializers.BUBBLE_RENDERER_SERIALIZER;
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

    public int getOffsetRange() {
        return offsetRange;
    }

    public float getMaxTime() {
        return maxTime;
    }

    public float getMinY() {
        return minY;
    }

    public float getMaxY() {
        return maxY;
    }

    public ResourceLocation getBubbleModelResourceLocation() {
        return bubbleModelResourceLocation;
    }

    public record Bubble(float x, float z, int offset, long time) {

    }

    public static class Serializer implements IHotpotSoupCustomElementRendererSerializer<HotpotBubbleRenderer> {
        public static final MapCodec<HotpotBubbleRenderer> CODEC = RecordCodecBuilder.mapCodec(renderer -> renderer.group(
                Codec.FLOAT.fieldOf("spread").forGetter(HotpotBubbleRenderer::getSpread),
                Codec.FLOAT.fieldOf("max_scale").forGetter(HotpotBubbleRenderer::getMaxScale),
                Codec.INT.fieldOf("amount").forGetter(HotpotBubbleRenderer::getAmount),
                Codec.INT.fieldOf("offset_range").forGetter(HotpotBubbleRenderer::getOffsetRange),
                Codec.FLOAT.fieldOf("max_time").forGetter(HotpotBubbleRenderer::getMaxTime),
                Codec.FLOAT.fieldOf("min_y").forGetter(HotpotBubbleRenderer::getMinY),
                Codec.FLOAT.fieldOf("max_y").forGetter(HotpotBubbleRenderer::getMaxY),
                ResourceLocation.CODEC.fieldOf("bubble_model_resource_location").forGetter(HotpotBubbleRenderer::getBubbleModelResourceLocation),
                Codec.BOOL.fieldOf("should_render_in_bowl").forGetter(HotpotBubbleRenderer::shouldRenderInBowl)
        ).apply(renderer, HotpotBubbleRenderer::new));

        @Override
        public MapCodec<HotpotBubbleRenderer> getCodec() {
            return CODEC;
        }
    }
}
