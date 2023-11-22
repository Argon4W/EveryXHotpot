package com.github.argon4w.hotpot.soups.renderers;

import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Optional;
import java.util.Random;

public class HotpotBubbleRenderer implements IHotpotSoupCustomElementRenderer {
    public static final Random RANDOM_SOURCE = new Random();
    public static final int BUBBLE_EMERGE_OFFSET_RANGE = 5;
    public static final float BUBBLE_GROWTH_TIME = 10f;
    public static final float BUBBLE_START_Y = 0.5f;
    public static final float BUBBLE_GROWTH_Y = 0.525f;

    private final Bubble[] bubbles;
    private final float spread, maxScale;
    private final ResourceLocation bubbleLocation;

    public HotpotBubbleRenderer(float spread, float maxScale, int amount, ResourceLocation bubbleLocation) {
        this.spread = spread;
        this.maxScale = maxScale;
        this.bubbles = new Bubble[amount];
        this.bubbleLocation = bubbleLocation;
    }

    private void renderBubble(BlockEntityRendererProvider.Context context, HotpotBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, Bubble bubble, BakedModel model) {
        poseStack.pushPose();

        float progress = (blockEntity.getTime() + bubble.offset) % BUBBLE_GROWTH_TIME / BUBBLE_GROWTH_TIME;
        float scale = progress * maxScale;
        float y = BUBBLE_START_Y + blockEntity.renderedWaterLevel * progress * BUBBLE_GROWTH_Y;

        poseStack.translate(bubble.x, y, bubble.z);
        poseStack.scale(scale, scale, scale);

        context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.translucent()), null, model, 1, 1, 1, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);

        poseStack.popPose();
    }

    @Override
    public void render(BlockEntityRendererProvider.Context context, HotpotBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float renderedWaterLevel) {
        BakedModel model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(bubbleLocation);

        for (int i = 0; i < bubbles.length; i++) {
            Bubble bubble = bubbles[i];

            if (bubble == null || blockEntity.getTime() >= bubble.startTime + bubble.offset + BUBBLE_GROWTH_TIME) {
                bubbles[i] = new Bubble(0.5f + (RANDOM_SOURCE.nextFloat() * 2f - 1f) * spread, 0.5f + (RANDOM_SOURCE.nextFloat() * 2f - 1f) * spread, RANDOM_SOURCE.nextInt(-BUBBLE_EMERGE_OFFSET_RANGE, BUBBLE_EMERGE_OFFSET_RANGE + 1), blockEntity.getTime());
                continue;
            }

            renderBubble(context, blockEntity, poseStack, bufferSource, combinedLight, combinedOverlay, bubble, model);
        }
    }

    public record Bubble(float x, float z, int offset, int startTime) {}
}
