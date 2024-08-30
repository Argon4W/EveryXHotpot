package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.SimpleItemSlot;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.placements.HotpotPlacedNapkinHolder;
import com.github.argon4w.hotpot.placements.coords.HotpotPlacementPositions;
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
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, IHotpotPlacementContainer container, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, LevelBlockPos pos) {
        if (!(placement instanceof HotpotPlacedNapkinHolder napkinHolder)) {
            return;
        }

        SimpleItemSlot napkinItemSlot = napkinHolder.getNapkinItemSlot();

        double x = HotpotPlacementPositions.getRenderCenterX(napkinHolder.getPosition());
        double z = HotpotPlacementPositions.getRenderCenterZ(napkinHolder.getPosition());

        int color = DyedItemColor.getOrDefault(napkinHolder.getNapkinHolderItemSlot().getItemStack(), -1);

        long posHashCode = pos.pos() != null ? pos.pos().hashCode() : 1L;
        long randomSeed = color * napkinHolder.getPosition() * posHashCode + napkinItemSlot.getItemStack().getCount() + 42L;

        RandomSource randomSource = RandomSource.create();
        randomSource.setSeed(randomSeed);
        double randomDegrees = Math.clamp(randomSource.nextGaussian(), 0.0, 1.0) * 15.0 - 7.5;

        double positionY = 0.5 * 0.68;
        double rotationY = 360.0 - napkinHolder.getDirection().toYRot() - randomDegrees;

        poseStack.pushPose();

        poseStack.translate(x, positionY, z);
        poseStack.mulPose(Axis.YP.rotationDegrees((float) rotationY));
        poseStack.scale(0.68f, 0.68f, 0.68f);

        context.getItemRenderer().renderStatic(napkinHolder.getNapkinHolderItemStack(), ItemDisplayContext.NONE, combinedLight, combinedOverlay, poseStack, bufferSource, null, 0);

        poseStack.popPose();
    }
}
