package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.SimpleItemSlot;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.github.argon4w.hotpot.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.items.HotpotPaperBowlItem;
import com.github.argon4w.hotpot.placements.HotpotPlacedPaperBowl;
import com.github.argon4w.hotpot.placements.HotpotPlacementSerializers;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class HotpotPlacedPaperBowlRenderer implements IHotpotPlacementRenderer {
    @Override
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, IHotpotPlacementContainerBlockEntity container, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, LevelBlockPos pos) {
        if (!(placement instanceof HotpotPlacedPaperBowl placedPaperBowl)) {
            return;
        }

        float x = HotpotPlacementSerializers.getSlotX(placedPaperBowl.getPos()) + 0.25f;
        float z = HotpotPlacementSerializers.getSlotZ(placedPaperBowl.getPos()) + 0.25f;

        SimpleItemSlot paperBowlItemSlot = placedPaperBowl.getPaperBowlItemSlot();
        ItemStack paperBowlItemStack = paperBowlItemSlot.getItemStack();

        int renderCount = HotpotPaperBowlItem.isPaperBowlClear(paperBowlItemStack) ? paperBowlItemSlot.getRenderCount() : 1;

        for (int i = 0; i < renderCount; i ++) {
            float scale = (i % 2 == 0) ? 0.6f : (0.6f - 0.0001f);
            float positionY = 0.001f + (0.5f - 3.0f / 16.0f) * 0.6f + 0.1f * i;
            float rotationY = placedPaperBowl.getDirection().toYRot() + 90.0f;

            poseStack.pushPose();
            poseStack.translate(x, positionY, z);
            poseStack.mulPose(Axis.YN.rotationDegrees(rotationY));
            poseStack.scale(scale, scale, scale);

            context.getItemRenderer().renderStatic(null, paperBowlItemStack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemDisplayContext.FIXED.ordinal());

            poseStack.popPose();
        }
    }
}
