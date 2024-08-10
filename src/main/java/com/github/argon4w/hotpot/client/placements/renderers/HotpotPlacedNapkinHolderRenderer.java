package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.SimpleItemSlot;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.github.argon4w.hotpot.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.placements.HotpotPlacedNapkinHolder;
import com.github.argon4w.hotpot.placements.HotpotPlacementSerializers;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.component.DyedItemColor;
import org.joml.Math;

public class HotpotPlacedNapkinHolderRenderer implements IHotpotPlacementRenderer {
    @Override
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, IHotpotPlacementContainerBlockEntity container, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, LevelBlockPos pos) {
        if (!(placement instanceof HotpotPlacedNapkinHolder napkinHolder)) {
            return;
        }

        SimpleItemSlot napkinItemSlot = napkinHolder.getNapkinItemSlot();

        float x = HotpotPlacementSerializers.getSlotX(napkinHolder.getPos()) + 0.25f;
        float z = HotpotPlacementSerializers.getSlotZ(napkinHolder.getPos()) + 0.25f;

        int color = DyedItemColor.getOrDefault(napkinHolder.getNapkinHolderItemSlot().getItemStack(), -1);

        RandomSource randomSource = RandomSource.create();
        randomSource.setSeed(color * napkinHolder.getPos() * (pos.pos() != null ? pos.pos().hashCode() : 1L) + napkinItemSlot.getItemStack().getCount() + 42L);
        float randomDegrees = Math.clamp((float) randomSource.nextGaussian(), 0.0f, 1.0f) * 15.0f - 7.5f;

        poseStack.pushPose();
        poseStack.translate(x, 0.5f * 0.68f, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(360.0f - napkinHolder.getDirection().toYRot() - randomDegrees));
        poseStack.scale(0.68f, 0.68f, 0.68f);

        context.getItemRenderer().renderStatic(napkinHolder.getNapkinHolderItemStack(), ItemDisplayContext.NONE, combinedLight, combinedOverlay, poseStack, bufferSource, null, 0);

        poseStack.popPose();
    }
}
