package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.SimpleItemSlot;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.client.blocks.ISectionGeometryBLockEntityRenderer;
import com.github.argon4w.hotpot.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.placements.HotpotLongPlate;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
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
        if (!(placement instanceof HotpotLongPlate longPlate)) {
            return;
        }

        int position1 = longPlate.getPosition1();
        ComplexDirection direction = ComplexDirection.between(longPlate.getPosition1(), longPlate.getPosition2());

        double x1 = HotpotPlacementPositions.getRenderCenterX(position1);
        double z1 = HotpotPlacementPositions.getRenderCenterZ(position1);

        int plateCount = longPlate.getPlateItemSlot().getRenderCount(8);;
        int i = 0;

        for (int k = 0; k < longPlate.getItemSlot1().getRenderCount(); k ++, i ++) {
            renderLongPlateItem(direction, context, poseStack, bufferSource, combinedLight, combinedOverlay, longPlate.getItemSlot1(), x1, z1, i, plateCount);
        }

        for (int k = 0; k < longPlate.getItemSlot2().getRenderCount(); k ++, i ++) {
            renderLongPlateItem(direction, context, poseStack, bufferSource, combinedLight, combinedOverlay, longPlate.getItemSlot2(), x1, z1, i, plateCount);
        }
    }

    @Override
    public void renderSectionGeometry(IHotpotPlacement placement, AddSectionGeometryEvent.SectionRenderingContext context, IHotpotPlacementContainer container, BlockPos pos, PoseStack poseStack, ISectionGeometryBLockEntityRenderer.ModelRenderer modelRenderer) {
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

        for (int plateCount = 0; plateCount < longPlate.getPlateItemSlot().getRenderCount(8); plateCount ++) {
            double positionY = plateCount * 0.0625f;

            poseStack.pushPose();
            poseStack.translate(positionX, positionY, positionZ);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) direction.toYRot()));
            poseStack.scale(0.68f, 0.68f, 0.68f);

            BakedModel model = Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "block/hotpot_plate_long")));
            modelRenderer.renderModel(model, poseStack, RenderType.solid(), OverlayTexture.NO_OVERLAY, ModelData.EMPTY);

            poseStack.popPose();
        }
    }

    public void renderLongPlateItem(ComplexDirection direction, BlockEntityRendererProvider.Context context, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, SimpleItemSlot slot, double x, double z, int index, int plateCount) {
        double positionY = 0.0175 + 0.0625 * plateCount;
        double positionZ = -0.07 + index * 0.09;

        poseStack.pushPose();
        poseStack.translate(x, positionY, z);
        poseStack.mulPose(Axis.YN.rotationDegrees((float) direction.toYRot()));
        poseStack.translate(0f, 0f, positionZ);
        poseStack.mulPose(Axis.XN.rotationDegrees(75));
        poseStack.scale(0.35f, 0.35f, 0.35f);

        context.getItemRenderer().renderStatic(slot.getItemStack(), ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, null, ItemDisplayContext.GROUND.ordinal());

        poseStack.popPose();
    }
}
