package com.github.argon4w.hotpot;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Math;

public class HotpotBlockEntityRenderer implements BlockEntityRenderer<HotpotBlockEntity> {
    public static final RandomSource RANDOM_SOURCE = RandomSource.createNewThreadLocalInstance();
    public static final int BUBBLE_EMERGE_OFFSET_RANGE = 5;
    public static final float BUBBLE_GROWTH_TIME = 10f;
    public static final float BUBBLE_MAX_SCALE = 0.6f;
    public static final float BUBBLE_START_Y = 0.5f;
    public static final float BUBBLE_GROWTH_Y = 0.525f;
    public static final float BUBBLE_SPREAD = 0.35f;
    public static final float ITEM_ROUND_TRIP_TIME = 60f;
    public static final float ITEM_RADIUS = 0.315f;
    public static final float ITEM_START_Y = 0.45f + 0.5f;
    public static final float ITEM_FLOAT_Y = 0.06f;
    public static final float ITEM_ROTATION = 25f;
    public static final float ITEM_SCALE = 0.25f;

    private final BlockEntityRendererProvider.Context context;
    private final Bubble[] bubbles;

    public HotpotBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
        bubbles = new Bubble[50];
    }

    private void renderBubble(HotpotBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, Bubble bubble){
        poseStack.pushPose();

        float progress = (blockEntity.getTime() + bubble.offset) % BUBBLE_GROWTH_TIME / BUBBLE_GROWTH_TIME;
        float scale = progress * BUBBLE_MAX_SCALE;
        float y = BUBBLE_START_Y + progress * BUBBLE_GROWTH_Y;

        poseStack.translate(bubble.x, y, bubble.z);
        poseStack.scale(scale, scale, scale);

        BakedModel model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModelBakery().getBakedTopLevelModels().get(new ResourceLocation(HotpotModEntry.MODID, "effect/hotpot_bubble"));
        context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.translucent()), null, model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, RenderType.translucent());

        poseStack.popPose();
    }

    private void renderItem(HotpotBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, ItemStack stack, float offset) {
        poseStack.pushPose();

        float f = blockEntity.getTime() / 20f / ITEM_ROUND_TRIP_TIME + offset;

        poseStack.translate(0.5f + Math.sin(f * 2f * Math.PI) * ITEM_RADIUS, ITEM_START_Y + Math.sin((f + 0.125f * f) / 0.25f * 2f  * Math.PI) * ITEM_FLOAT_Y, 0.5f + Math.cos(f * 2f * Math.PI) * ITEM_RADIUS);
        poseStack.mulPose(Axis.YP.rotationDegrees(f * 360f));
        poseStack.mulPose(Axis.XP.rotationDegrees(-90f + ITEM_ROTATION));
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

        context.getItemRenderer().renderStatic(null, stack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, blockEntity.getLevel(), combinedLight, combinedOverlay, ItemDisplayContext.FIXED.ordinal());

        poseStack.popPose();
    }

    @Override
    public void render(HotpotBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        for (int i = 0; i < blockEntity.getItems().size(); i ++) {
            renderItem(blockEntity, poseStack, bufferSource, combinedLight, combinedOverlay, blockEntity.getItems().get(i), 0.125f * i);
        }

        for (int i = 0; i < bubbles.length; i ++) {
            Bubble bubble = bubbles[i];

            if (bubble == null || blockEntity.getTime() >= bubble.startTime + bubble.offset + BUBBLE_GROWTH_TIME) {
                bubbles[i] = new Bubble(0.5f + (RANDOM_SOURCE.nextFloat() * 2f - 1f) * BUBBLE_SPREAD, 0.5f + (RANDOM_SOURCE.nextFloat() * 2f - 1f) * BUBBLE_SPREAD, RANDOM_SOURCE.nextInt(-BUBBLE_EMERGE_OFFSET_RANGE, BUBBLE_EMERGE_OFFSET_RANGE + 1), blockEntity.getTime());
                continue;
            }

            renderBubble(blockEntity, poseStack, bufferSource, combinedLight, combinedOverlay, bubble);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(HotpotBlockEntity p_112306_) {
        return false;
    }

    @Override
    public int getViewDistance() {
        return 24;
    }

    public record Bubble(float x, float z, int offset, int startTime) {}
}
