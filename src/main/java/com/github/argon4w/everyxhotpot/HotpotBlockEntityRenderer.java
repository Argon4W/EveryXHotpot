package com.github.argon4w.everyxhotpot;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Math;

import java.util.LinkedList;

public class HotpotBlockEntityRenderer implements BlockEntityRenderer<HotpotBlockEntity> {
    private final BlockEntityRendererProvider.Context context;
    private final Bubble[] bubbles;
    private final RandomSource randomSource;

    public HotpotBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
        bubbles = new Bubble[50];
        randomSource = RandomSource.create();
    }

    @Override
    public boolean shouldRenderOffScreen(HotpotBlockEntity p_112306_) {
        return true;
    }

    @Override
    public void render(HotpotBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        for (int i = 0; i < blockEntity.getItems().size(); i ++) {
            renderItemInSoup(blockEntity, poseStack, bufferSource, combinedLight, combinedOverlay, blockEntity.getItems().get(i), 0.125f * i);
        }

        for (int i = 0; i < 50; i ++) {
            Bubble bubble = bubbles[i];

            if (bubble == null || blockEntity.getTime() >= bubble.startTime + bubble.offset + 10) {
                bubbles[i] = new Bubble(0.5f + (randomSource.nextFloat() * 2f - 1f) * 0.35f, 0.5f + (randomSource.nextFloat() * 2f - 1f) * 0.35f, randomSource.nextInt(-5, 6), blockEntity.getTime());
                continue;
            }

            renderBubble(blockEntity, poseStack, bufferSource, combinedLight, combinedOverlay, bubble);
        }
    }

    private void renderBubble(HotpotBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, Bubble bubble){
        poseStack.pushPose();

        float progress = (blockEntity.getTime() + bubble.offset) % 10f / 10f;
        float scale = progress * 0.6f;
        float y = 0.2f + progress * 0.85f;

        poseStack.translate(bubble.x, y, bubble.z);
        poseStack.scale(scale, scale, scale);

        BakedModel model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModelBakery().getBakedTopLevelModels().get(new ResourceLocation("everyxhotpot", "effect/hotpot_bubble"));
        context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.translucent()), null, model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, RenderType.translucent());

        poseStack.popPose();
    }

    private void renderItemInSoup(HotpotBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, ItemStack stack, float offset) {
        poseStack.pushPose();

        float f = blockEntity.getTime() / 20f / 60f + offset;

        poseStack.translate(0.5f + Math.sin(f * 2f * Math.PI) * 0.35f, 0.45f + 0.5f + Math.sin((f + 0.125f * f) / 0.25f * 2f  * Math.PI) * 0.06f, 0.5f + Math.cos(f * 2f * Math.PI) * 0.35f);
        poseStack.mulPose(Axis.YP.rotationDegrees(f * 360f));
        poseStack.mulPose(Axis.XP.rotationDegrees(-90f + 25f));
        poseStack.scale(0.25f, 0.25f, 0.25f);

        context.getItemRenderer().renderStatic(null, stack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, blockEntity.getLevel(), combinedLight, combinedOverlay, ItemDisplayContext.FIXED.ordinal());

        poseStack.popPose();
    }

    public record Bubble(float x, float z, int offset, int startTime) {}
}
