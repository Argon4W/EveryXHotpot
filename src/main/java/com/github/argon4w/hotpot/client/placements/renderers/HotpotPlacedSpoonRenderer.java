package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.placements.HotpotPlacedSpoon;
import com.github.argon4w.hotpot.placements.coords.ComplexDirection;
import com.github.argon4w.hotpot.placements.coords.HotpotPlacementPositions;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;

public class HotpotPlacedSpoonRenderer implements IHotpotPlacementRenderer {
    @Override
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, IHotpotPlacementContainer container, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, LevelBlockPos pos) {
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

        context.getItemRenderer().renderStatic(null, placedSpoon.getSpoonItemSlot().getItemStack(), ItemDisplayContext.NONE, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemDisplayContext.FIXED.ordinal());

        poseStack.popPose();
    }
}
