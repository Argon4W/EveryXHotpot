package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.placements.HotpotPlacedChopstick;
import com.github.argon4w.hotpot.placements.HotpotPlacedSpoon;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;

public class HotpotPlacedSpoonRenderer implements IHotpotPlacementRenderer {
    @Override
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, HotpotPlacementBlockEntity hotpotBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (!(placement instanceof HotpotPlacedSpoon placedSpoon)) {
            return;
        }

        float x1 = IHotpotPlacement.getSlotX(placedSpoon.getPos1()) + 0.25f;
        float z1 = IHotpotPlacement.getSlotZ(placedSpoon.getPos1()) + 0.25f;

        float x2 = IHotpotPlacement.getSlotX(placedSpoon.getPos2()) + 0.25f;
        float z2 = IHotpotPlacement.getSlotZ(placedSpoon.getPos2()) + 0.25f;

        poseStack.pushPose();
        poseStack.translate((x1 + x2) / 2, 0.12f, (z1 + z2) / 2);
        poseStack.mulPose(Axis.YN.rotationDegrees(placedSpoon.getDirection().toYRot() + 180f));
        poseStack.translate(0, 0, -0.24);
        poseStack.mulPose(Axis.XP.rotationDegrees(137));
        poseStack.scale(0.5f, 0.5f, 0.5f);

        context.getItemRenderer().renderStatic(null, placedSpoon.getSpoonItemStack(), ItemDisplayContext.NONE, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemDisplayContext.FIXED.ordinal());

        poseStack.popPose();
    }
}
