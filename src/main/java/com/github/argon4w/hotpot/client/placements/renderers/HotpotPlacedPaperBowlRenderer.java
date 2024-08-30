package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.SimpleItemSlot;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.items.HotpotPaperBowlItem;
import com.github.argon4w.hotpot.placements.HotpotPlacedPaperBowl;
import com.github.argon4w.hotpot.placements.coords.HotpotPlacementPositions;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class HotpotPlacedPaperBowlRenderer implements IHotpotPlacementRenderer {
    @Override
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, IHotpotPlacementContainer container, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, LevelBlockPos pos) {
        if (!(placement instanceof HotpotPlacedPaperBowl placedPaperBowl)) {
            return;
        }

        double x = HotpotPlacementPositions.getRenderCenterX(placedPaperBowl.getPosition());
        double z = HotpotPlacementPositions.getRenderCenterZ(placedPaperBowl.getPosition());

        SimpleItemSlot paperBowlItemSlot = placedPaperBowl.getPaperBowlItemSlot();
        ItemStack paperBowlItemStack = paperBowlItemSlot.getItemStack();

        int renderCount = HotpotPaperBowlItem.isPaperBowlClear(paperBowlItemStack) ? paperBowlItemSlot.getRenderCount() : 1;

        for (int i = 0; i < renderCount; i ++) {
            double scale = (i % 2 == 0) ? 0.6 : (0.6 - 0.0001);
            double positionY = 0.001 + (0.5 - 3.0 / 16.0) * 0.6 + 0.1 * i;
            double rotationY = placedPaperBowl.getDirection().toYRot() + 90.0;

            poseStack.pushPose();
            poseStack.translate(x, positionY, z);
            poseStack.mulPose(Axis.YN.rotationDegrees((float) rotationY));
            poseStack.scale((float) scale, (float) scale, (float) scale);

            context.getItemRenderer().renderStatic(null, paperBowlItemStack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemDisplayContext.FIXED.ordinal());

            poseStack.popPose();
        }
    }
}
