package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.placements.HotpotPlacedPaperBowl;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;

public class HotpotPlacedPaperBowlRenderer implements IHotpotPlacementRenderer {
    @Override
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, HotpotPlacementBlockEntity hotpotBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (!(placement instanceof HotpotPlacedPaperBowl placedPaperBowl)) {
            return;
        }

        float x = IHotpotPlacement.getSlotX(placedPaperBowl.getPos1()) + 0.25f;
        float z = IHotpotPlacement.getSlotZ(placedPaperBowl.getPos1()) + 0.25f;

        poseStack.pushPose();
        poseStack.translate(x, 0.2f, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(placedPaperBowl.getDirection().toYRot() - 90));
        poseStack.scale(0.6f, 0.6f, 0.6f);

        context.getItemRenderer().renderStatic(null, placedPaperBowl.getPaperBowlItemStack(), ItemDisplayContext.FIXED, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemDisplayContext.FIXED.ordinal());

        poseStack.popPose();
    }
}
