package com.github.argon4w.hotpot.soups.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.data.EmptyModelData;

public class HotpotSpicySoupFloatingPepperRenderer implements IHotpotSoupCustomElementRenderer {
    @Override
    public void render(BlockEntityRendererProvider.Context context, HotpotBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float renderedWaterLevel) {
        float f = blockEntity.getTime() / 20f / 5f;

        float part1Rotation = (float) Math.sin(f * (float) Math.PI) * 1.6f;
        float part2Rotation = (float) Math.sin((f + 1f) * (float) Math.PI) * 1.6f;

        float part1Position = (float) Math.cos(f * (float) Math.PI) * 0.02f + renderedWaterLevel * 0.4375f + 0.5625f - 0.01f;
        float part2Position = (float) Math.cos((f + 1f) * (float) Math.PI) * 0.02f + renderedWaterLevel * 0.4375f + 0.5625f - 0.01f;

        poseStack.pushPose();
        poseStack.translate(0f, part1Position, 0f);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(part1Rotation));

        BakedModel part1Model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_spicy_soup_floating_pepper_1"));
        context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.translucent()), null, part1Model, 1, 1, 1, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);

        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0f, part2Position, 0f);
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(part2Rotation));

        BakedModel part2Model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_spicy_soup_floating_pepper_2"));
        context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.translucent()), null, part2Model, 1, 1, 1, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);

        poseStack.popPose();
    }
}
