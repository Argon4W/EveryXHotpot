package com.github.argon4w.hotpot.client.contents.renderers.items;

import com.github.argon4w.hotpot.client.contents.IHotpotItemContentSpecialRenderer;
import com.github.argon4w.hotpot.contents.AbstractHotpotItemStackContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Math;

public class HotpotSkewerItemContentRenderer implements IHotpotItemContentSpecialRenderer {
    @Override
    public void render(AbstractHotpotItemStackContent itemStackContent, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, double waterLevel, double rotation, double x, double z) {
        poseStack.pushPose();

        double positionX = 0.5 + x * 0.315;
        double positionZ = 0.5 + z * 0.315;
        double positionY = 0.56 - getFloatingCurve(rotation / 360.0, 0.0f) * 0.03 + 0.42 * waterLevel;

        double degree = - (Math.toDegrees(Math.safeAsin( (waterLevel - 0.35) / (1.0 - 0.35))) / 90.0) * 85.0;
        double rotationY = rotation + 20.0;
        double rotationX = 270.0 + degree;

        poseStack.translate(positionX, positionY, positionZ);

        poseStack.mulPose(Axis.YP.rotationDegrees((float) rotationY));
        poseStack.mulPose(Axis.XN.rotationDegrees((float) rotationX));

        poseStack.scale(0.32f, 0.32f, 0.32f);

        Minecraft.getInstance().getItemRenderer().renderStatic(null, itemStackContent.getItemStack(), ItemDisplayContext.NONE, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemDisplayContext.NONE.ordinal());

        poseStack.popPose();
    }

    public double getFloatingCurve(double f, double offset) {
        return Math.sin((f + offset) / 0.25 * 2 * Math.PI);
    }
}
