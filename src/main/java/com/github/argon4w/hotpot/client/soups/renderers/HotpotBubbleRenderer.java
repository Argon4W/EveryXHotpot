package com.github.argon4w.hotpot.client.soups.renderers;

import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.client.soups.IHotpotSoupCustomElementRenderer;
import com.github.argon4w.hotpot.client.soups.IHotpotSoupCustomElementRendererSerializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraftforge.client.model.data.ModelData;

public class HotpotBubbleRenderer implements IHotpotSoupCustomElementRenderer {
    public static final RandomSource RANDOM_SOURCE = RandomSource.createNewThreadLocalInstance();
    public static final int BUBBLE_EMERGE_OFFSET_RANGE = 5;
    public static final float BUBBLE_GROWTH_TIME = 10f;
    public static final float BUBBLE_START_Y = 0.5f;
    public static final float BUBBLE_GROWTH_Y = 0.525f;

    private final Bubble[] bubbles;
    private final float spread, maxScale;
    private final ResourceLocation bubbleLocation;
    private final boolean shouldRenderInBowl;

    public HotpotBubbleRenderer(float spread, float maxScale, int amount, ResourceLocation bubbleLocation, boolean shouldRenderInBowl) {
        this.spread = spread;
        this.maxScale = maxScale;
        this.bubbles = new Bubble[amount];
        this.bubbleLocation = bubbleLocation;
        this.shouldRenderInBowl = shouldRenderInBowl;
    }

    @Override
    public void render(BlockEntityRendererProvider.Context context, int time, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float renderedWaterLevel) {
        BakedModel model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(bubbleLocation);

        for (int i = 0; i < bubbles.length; i++) {
            Bubble bubble = bubbles[i];

            if (bubble == null || time >= bubble.startTime + bubble.offset + BUBBLE_GROWTH_TIME) {
                bubbles[i] = new Bubble(0.5f + (RANDOM_SOURCE.nextFloat() * 2f - 1f) * spread, 0.5f + (RANDOM_SOURCE.nextFloat() * 2f - 1f) * spread, RANDOM_SOURCE.nextInt(-BUBBLE_EMERGE_OFFSET_RANGE, BUBBLE_EMERGE_OFFSET_RANGE + 1), time);
                continue;
            }

            renderBubble(context, time, poseStack, bufferSource, combinedLight, combinedOverlay, bubble, model, renderedWaterLevel);
        }
    }

    @Override
    public boolean shouldRenderInBowl() {
        return shouldRenderInBowl;
    }

    public void renderBubble(BlockEntityRendererProvider.Context context, int time, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, Bubble bubble, BakedModel model, float renderedWaterLevel) {
        poseStack.pushPose();

        float progress = (time + bubble.offset) % BUBBLE_GROWTH_TIME / BUBBLE_GROWTH_TIME;
        float scale = progress * this.maxScale;
        float y = BUBBLE_START_Y + renderedWaterLevel * progress * BUBBLE_GROWTH_Y;

        poseStack.translate(bubble.x, y, bubble.z);
        poseStack.scale(scale, scale, scale);

        context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.translucent()), null, model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, RenderType.translucent());

        poseStack.popPose();
    }

    public record Bubble(float x, float z, int offset, int startTime) {

    }

    public static class Serializer implements IHotpotSoupCustomElementRendererSerializer<HotpotBubbleRenderer> {
        @Override
        public HotpotBubbleRenderer fromJson(JsonObject jsonObject) {
            if (!jsonObject.has("bubble_model_location")) {
                throw new JsonParseException("Bubble renderer must have a \"bubble_model_location\"");
            }

            if (!ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(jsonObject, "bubble_model_location"))) {
                throw new JsonParseException("\"bubble_model_location\" in the bubble renderer must be a valid resource location");
            }

            if (!jsonObject.has("spread")) {
                throw new JsonParseException("Bubble renderer must have a \"spread\"");
            }

            if (!jsonObject.has("max_scale")) {
                throw new JsonParseException("Bubble renderer must have a \"max_scale\"");
            }

            if (!jsonObject.has("amount")) {
                throw new JsonParseException("Bubble renderer must have a \"amount\"");
            }

            if (!jsonObject.has("should_render_in_bowl")) {
                throw new JsonParseException("Bubble renderer must have a \"should_render_in_bowl\"");
            }

            ResourceLocation bubbleResourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "bubble_model_location"));
            float spread = GsonHelper.getAsFloat(jsonObject, "spread");
            float maxScale = GsonHelper.getAsFloat(jsonObject, "max_scale");
            int amount = GsonHelper.getAsInt(jsonObject, "amount");
            boolean shouldRenderInBowl = GsonHelper.getAsBoolean(jsonObject, "should_render_in_bowl");

            return new HotpotBubbleRenderer(spread, maxScale, amount, bubbleResourceLocation, shouldRenderInBowl);
        }
    }
}
