package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.api.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.api.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.client.sections.ISectionGeometryRenderContext;
import com.github.argon4w.hotpot.placements.HotpotPlacedSpoon;
import com.github.argon4w.hotpot.api.placements.IHotpotPlacement;
import com.github.argon4w.hotpot.placements.coords.ComplexDirection;
import com.github.argon4w.hotpot.placements.coords.HotpotPlacementPositions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;

public class HotpotPlacedSpoonRenderer implements IHotpotPlacementRenderer {
    @Override
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, IHotpotPlacementContainer container, BlockPos pos, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float partialTick) {

    }

    @Override
    public void renderSectionGeometry(IHotpotPlacement placement, AddSectionGeometryEvent.SectionRenderingContext context, IHotpotPlacementContainer container, BlockPos pos, PoseStack poseStack, ISectionGeometryRenderContext modelRenderContext) {
        if (!(placement instanceof HotpotPlacedSpoon placedSpoon)) {
            return;
        }

        int position1 = placedSpoon.getPosition1();
        int position2 = placedSpoon.getPosition2();
        ComplexDirection direction = ComplexDirection.between(position1, position2);

        double x1 = HotpotPlacementPositions.getRenderCenterX(position1);
        double z1 = HotpotPlacementPositions.getRenderCenterZ(position1);

        double x2 = HotpotPlacementPositions.getRenderCenterX(position2);
        double z2 = HotpotPlacementPositions.getRenderCenterZ(position2);

        double positionX = (x1 + x2) / 2;
        double positionZ = (z1 + z2) / 2;

        double rotationY = direction.toYRot() + 180f;

        poseStack.pushPose();
        poseStack.translate(positionX, 0.12, positionZ);
        poseStack.mulPose(Axis.YN.rotationDegrees((float) rotationY));
        poseStack.translate(0, 0, -0.24);
        poseStack.mulPose(Axis.XP.rotationDegrees(137));
        poseStack.scale(0.5f, 0.5f, 0.5f);

        modelRenderContext.renderUncachedItem(placedSpoon.getSpoonItemSlot().getItemStack(), ItemDisplayContext.NONE, false, poseStack, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }
}
