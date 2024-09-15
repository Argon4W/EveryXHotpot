package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.client.blocks.ISectionGeometryBLockEntityRenderer;
import com.github.argon4w.hotpot.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.placements.HotpotSmallPlate;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
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

public class HotpotSmallPlateRenderer implements IHotpotPlacementRenderer {
    @Override
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, IHotpotPlacementContainer container, BlockPos pos, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float partialTick) {
        if (!(placement instanceof HotpotSmallPlate smallPlate)) {
            return;
        }

        double x = HotpotPlacementPositions.getRenderCenterX(smallPlate.getPosition());
        double z = HotpotPlacementPositions.getRenderCenterZ(smallPlate.getPosition());

        for (int i = 0; i < smallPlate.getItemSlot().getRenderCount(); i ++) {
            double positionY = smallPlate.getPlateItemSlot().getRenderCount(8) * 0.0625 + 0.02 * i;
            double rotationY = smallPlate.getDirection().toYRot() + (i % 2) * 20;

            poseStack.pushPose();

            poseStack.translate(x, positionY, z);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) rotationY));
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            poseStack.scale(0.35f, 0.35f, 0.35f);

            context.getItemRenderer().renderStatic(smallPlate.getItemSlot().getItemStack(), ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, null, ItemDisplayContext.GROUND.ordinal());

            poseStack.popPose();
        }
    }

    @Override
    public void renderSectionGeometry(IHotpotPlacement placement, AddSectionGeometryEvent.SectionRenderingContext context, IHotpotPlacementContainer container, BlockPos pos, PoseStack poseStack, ISectionGeometryBLockEntityRenderer.ModelRenderer modelRenderer) {
        if (!(placement instanceof HotpotSmallPlate smallPlate)) {
            return;
        }

        double x = HotpotPlacementPositions.getRenderCenterX(smallPlate.getPosition());
        double z = HotpotPlacementPositions.getRenderCenterZ(smallPlate.getPosition());

        for (int plateCount = 0; plateCount < smallPlate.getPlateItemSlot().getRenderCount(8); plateCount++) {
            double positionY = plateCount * 0.0625;

            poseStack.pushPose();
            poseStack.translate(x, positionY, z);
            poseStack.scale(0.68f, 0.68f, 0.68f);

            BakedModel model = Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "block/hotpot_plate_small")));
            modelRenderer.renderModel(model, poseStack, RenderType.solid(), OverlayTexture.NO_OVERLAY, ModelData.EMPTY);

            poseStack.popPose();
        }
    }
}
