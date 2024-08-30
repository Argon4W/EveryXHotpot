package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.SimpleItemSlot;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.placements.HotpotLargeRoundPlate;
import com.github.argon4w.hotpot.placements.coords.ComplexDirection;
import com.github.argon4w.hotpot.placements.coords.HotpotPlacementPositions;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.data.ModelData;

public class HotpotLargeRoundPlateRenderer implements IHotpotPlacementRenderer {
    @Override
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, IHotpotPlacementContainer container, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, LevelBlockPos pos) {
        if (!(placement instanceof HotpotLargeRoundPlate largeRoundPlate)) {
            return;
        }

        int position1 = largeRoundPlate.getPosition1();
        int position2 = largeRoundPlate.getPosition2();
        int position3 = largeRoundPlate.getPosition3();

        double centerX = (HotpotPlacementPositions.getRenderCenterX(position1) + HotpotPlacementPositions.getRenderCenterX(position3)) / 2;
        double centerZ = (HotpotPlacementPositions.getRenderCenterZ(position1) + HotpotPlacementPositions.getRenderCenterZ(position3)) / 2;
        ComplexDirection direction = ComplexDirection.between(position1, position2);

        int plateCount = 0;

        for (; plateCount < largeRoundPlate.getPlateItemSlot().getRenderCount(8); plateCount++) {
            float positionY = plateCount * 0.0625f;

            poseStack.pushPose();

            poseStack.translate(centerX, positionY, centerZ);
            poseStack.scale(0.66f, 0.66f, 0.66f);

            BakedModel model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "block/hotpot_plate_large_round")));
            context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), null, model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());

            poseStack.popPose();
        }

        poseStack.pushPose();

        for (int i = 0; i < 4; i ++) {
            renderLargeRoundPlateItem(context, poseStack, bufferSource, combinedLight, combinedOverlay, largeRoundPlate.getItemSlots().get(largeRoundPlate.getPositions().get(i)), plateCount, i, centerX, centerZ, direction);
        }

        poseStack.popPose();
    }

    public void renderLargeRoundPlateItem(BlockEntityRendererProvider.Context context, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, SimpleItemSlot slot, int plateCount, int i, double centerX, double centerZ, ComplexDirection direction) {
        double startDegree = 360.0 / 4.0 * i - direction.toYRot();

        for (int j = 0; j < slot.getRenderCount(); j ++) {
            double stepDegree = 360.0 / 16.0 * j;
            double positionY = 0.0175 + 0.0625 * plateCount;
            double rotationY = 90.0f + startDegree + stepDegree;

            poseStack.pushPose();

            poseStack.translate(centerX, positionY, centerZ);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) rotationY));
            poseStack.translate(0.25f, 0.0f, 0.0f);
            poseStack.mulPose(Axis.XP.rotationDegrees(75));

            poseStack.scale(0.35f, 0.35f, 0.35f);

            context.getItemRenderer().renderStatic(slot.getItemStack(), ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, null, ItemDisplayContext.FIXED.ordinal());

            poseStack.popPose();
        }
    }
}
