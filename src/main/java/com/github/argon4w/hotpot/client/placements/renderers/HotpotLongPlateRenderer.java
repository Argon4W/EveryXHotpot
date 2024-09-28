package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.SimpleItemSlot;
import com.github.argon4w.hotpot.api.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.api.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.client.sections.ISectionGeometryRenderContext;
import com.github.argon4w.hotpot.placements.HotpotLongPlate;
import com.github.argon4w.hotpot.api.placements.IHotpotPlacement;
import com.github.argon4w.hotpot.placements.coords.ComplexDirection;
import com.github.argon4w.hotpot.placements.coords.HotpotPlacementPositions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.neoforged.neoforge.client.model.data.ModelData;

public class HotpotLongPlateRenderer implements IHotpotPlacementRenderer {

    @Override
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, IHotpotPlacementContainer container, BlockPos pos, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float partialTick) {

    }

    @Override
    public void renderSectionGeometry(IHotpotPlacement placement, AddSectionGeometryEvent.SectionRenderingContext context, IHotpotPlacementContainer container, BlockPos pos, PoseStack poseStack, ISectionGeometryRenderContext modelRenderContext) {
        if (!(placement instanceof HotpotLongPlate longPlate)) {
            return;
        }

        int position1 = longPlate.getPosition1();
        int position2 = longPlate.getPosition2();
        ComplexDirection direction = ComplexDirection.between(longPlate.getPosition1(), longPlate.getPosition2());

        double x1 = HotpotPlacementPositions.getRenderCenterX(position1);
        double z1 = HotpotPlacementPositions.getRenderCenterZ(position1);

        double x2 = HotpotPlacementPositions.getRenderCenterX(position2);
        double z2 = HotpotPlacementPositions.getRenderCenterZ(position2);

        double positionX = (x1 + x2) / 2;
        double positionZ = (z1 + z2) / 2;

        int plateCount = 0;
        int i = 0;

        for (; plateCount < longPlate.getPlateItemSlot().getRenderCount(8); plateCount ++) {
            double positionY = plateCount * 0.0625f;

            poseStack.pushPose();
            poseStack.translate(positionX, positionY, positionZ);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) direction.toYRot()));
            poseStack.scale(0.68f, 0.68f, 0.68f);

            BakedModel model = Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "block/hotpot_plate_long")));
            modelRenderContext.renderCachedModel(model, poseStack, RenderType.solid(), OverlayTexture.NO_OVERLAY, ModelData.EMPTY);

            poseStack.popPose();
        }

        for (int k = 0; k < longPlate.getItemSlot1().getRenderCount(); k ++, i ++) {
            renderLongPlateItem(modelRenderContext, poseStack, longPlate.getItemSlot1(), x1, z1, i, plateCount, direction);
        }

        for (int k = 0; k < longPlate.getItemSlot2().getRenderCount(); k ++, i ++) {
            renderLongPlateItem(modelRenderContext, poseStack, longPlate.getItemSlot2(), x1, z1, i, plateCount, direction);
        }
    }

    public void renderLongPlateItem(ISectionGeometryRenderContext modelRenderContext, PoseStack poseStack, SimpleItemSlot slot, double x, double z, int index, int plateCount, ComplexDirection direction) {
        double positionY = 0.0175 + 0.0625 * plateCount;
        double positionZ = -0.07 + index * 0.09;

        poseStack.pushPose();
        poseStack.translate(x, positionY, z);
        poseStack.mulPose(Axis.YN.rotationDegrees((float) direction.toYRot()));
        poseStack.translate(0f, 0f, positionZ);
        poseStack.mulPose(Axis.XN.rotationDegrees(75));
        poseStack.scale(0.35f, 0.35f, 0.35f);

        modelRenderContext.renderUncachedItem(slot.getItemStack(), ItemDisplayContext.FIXED, false, poseStack, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }
}
