package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.github.argon4w.hotpot.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.items.HotpotPaperBowlItem;
import com.github.argon4w.hotpot.placements.HotpotPlacedPaperBowl;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class HotpotPlacedPaperBowlRenderer implements IHotpotPlacementRenderer {
    @Override
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, IHotpotPlacementContainerBlockEntity container, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (!(placement instanceof HotpotPlacedPaperBowl placedPaperBowl)) {
            return;
        }

        float x = IHotpotPlacement.getSlotX(placedPaperBowl.getPos()) + 0.25f;
        float z = IHotpotPlacement.getSlotZ(placedPaperBowl.getPos()) + 0.25f;

        ItemStack paperBowlItemStack = placedPaperBowl.getPaperBowlItemSlot().getItemStack();

        int renderCount = HotpotPaperBowlItem.isPaperBowlClear(paperBowlItemStack) ? Math.max(1, (paperBowlItemStack.getCount() * 4 / paperBowlItemStack.getMaxStackSize())) : 1;

        for (int i = 0; i < renderCount; i ++) {
            float scale = (i % 2 == 0) ? 0.6f : (0.6f - 0.0001f);

            poseStack.pushPose();
            poseStack.translate(x, 0.2f + 0.1f * i, z);
            poseStack.mulPose(Axis.YN.rotationDegrees(placedPaperBowl.getDirection().toYRot() + 90.0f));
            poseStack.scale(scale, scale, scale);

            context.getItemRenderer().renderStatic(null, paperBowlItemStack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemDisplayContext.FIXED.ordinal());

            poseStack.popPose();
        }
    }
}
