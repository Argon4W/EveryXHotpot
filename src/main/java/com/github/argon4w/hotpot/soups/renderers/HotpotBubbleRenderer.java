package com.github.argon4w.hotpot.soups.renderers;

import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.EmptyModelData;

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

    private void renderBubble(TileEntityRendererDispatcher context, HotpotBlockEntity blockEntity, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay, Bubble bubble, IBakedModel model) {
        poseStack.pushPose();

        float progress = (blockEntity.getTime() + bubble.offset) % BUBBLE_GROWTH_TIME / BUBBLE_GROWTH_TIME;
        float scale = progress * maxScale;
        float y = BUBBLE_START_Y + blockEntity.renderedWaterLevel * progress * BUBBLE_GROWTH_Y;

        poseStack.translate(bubble.x, y, bubble.z);
        poseStack.scale(scale, scale, scale);

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.translucent()), null, model, 1, 1, 1, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);

        poseStack.popPose();
    }

    @Override
    public void render(TileEntityRendererDispatcher context, HotpotBlockEntity blockEntity, float partialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay, float renderedWaterLevel) {
        IBakedModel model = Minecraft.getInstance().getModelManager().getModel(bubbleLocation);

        for (int i = 0; i < bubbles.length; i++) {
            Bubble bubble = bubbles[i];

            if (bubble == null || blockEntity.getTime() >= bubble.startTime + bubble.offset + BUBBLE_GROWTH_TIME) {
                bubbles[i] = new Bubble(0.5f + (RANDOM_SOURCE.nextFloat() * 2f - 1f) * spread, 0.5f + (RANDOM_SOURCE.nextFloat() * 2f - 1f) * spread, - BUBBLE_EMERGE_OFFSET_RANGE + RANDOM_SOURCE.nextInt(2 * BUBBLE_EMERGE_OFFSET_RANGE + 1), blockEntity.getTime());
                continue;
            }

            renderBubble(context, blockEntity, poseStack, bufferSource, combinedLight, combinedOverlay, bubble, model);
        }
    }

    public class Bubble {
        private final float x;
        private final float z;
        private final int  offset;
        private final int startTime;

        public Bubble(float x, float z, int offset, int startTime) {
            this.x = x;
            this.z = z;
            this.offset = offset;
            this.startTime = startTime;
        }

        public float x() {
            return x;
        }

        public float z() {
            return z;
        }

        public int offset() {
            return offset;
        }

        public int startTime() {
            return startTime;
        }
    }
}
