package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.github.argon4w.hotpot.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.placements.HotpotPlacedSpoon;
import com.github.argon4w.hotpot.placements.HotpotPlacementSerializers;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;

public class HotpotPlacedSpoonRenderer implements IHotpotPlacementRenderer {
    @Override
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, IHotpotPlacementContainerBlockEntity container, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, LevelBlockPos pos) {
        if (!(placement instanceof HotpotPlacedSpoon placedSpoon)) {
            return;
        }

        float x1 = HotpotPlacementSerializers.getSlotX(placedSpoon.getPos1()) + 0.25f;
        float z1 = HotpotPlacementSerializers.getSlotZ(placedSpoon.getPos1()) + 0.25f;

        float x2 = HotpotPlacementSerializers.getSlotX(placedSpoon.getPos2()) + 0.25f;
        float z2 = HotpotPlacementSerializers.getSlotZ(placedSpoon.getPos2()) + 0.25f;

        float positionX = (x1 + x2) / 2;
        float positionZ = (z1 + z2) / 2;

        float rotationY = placedSpoon.getDirection().toYRot() + 180f;

        poseStack.pushPose();
        poseStack.translate(positionX, 0.12f, positionZ);
        poseStack.mulPose(Axis.YN.rotationDegrees(rotationY));
        poseStack.translate(0, 0, -0.24);
        poseStack.mulPose(Axis.XP.rotationDegrees(137));
        poseStack.scale(0.5f, 0.5f, 0.5f);

        context.getItemRenderer().renderStatic(null, placedSpoon.getSpoonItemSlot().getItemStack(), ItemDisplayContext.NONE, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemDisplayContext.FIXED.ordinal());

        poseStack.popPose();
    }
}
