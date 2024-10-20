package com.github.argon4w.hotpot.client.contents.renderers.items;

import com.github.argon4w.hotpot.api.client.contents.IHotpotItemContentSpecialRenderer;
import com.github.argon4w.hotpot.client.MappingBufferSource;
import com.github.argon4w.hotpot.contents.AbstractHotpotItemStackContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Math;

public class HotpotDefaultItemContentRenderer implements IHotpotItemContentSpecialRenderer {
    @Override
    public void render(AbstractHotpotItemStackContent itemStackContent, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, double waterLevel, double rotation, double x, double z) {
        poseStack.pushPose();

        double positionX = 0.5 + x * 0.315;
        double positionZ = 0.5 + z * 0.315;
        double positionY = 0.53 - curve(rotation / 360.0, 0.0) * 0.06 + 0.42 * waterLevel;

        double rotationY = rotation - 90.0;
        double rotationX = curve(rotation / 360.0, 1) * 25.0;

        poseStack.translate(positionX, positionY, positionZ);

        poseStack.mulPose(Axis.YP.rotationDegrees((float) rotationY));
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0f));
        poseStack.mulPose(Axis.XP.rotationDegrees((float) rotationX));

        poseStack.scale(0.25f, 0.25f, 0.25f);

        Minecraft.getInstance().getItemRenderer().renderStatic(null, itemStackContent.getItemStack(), ItemDisplayContext.FIXED, true, poseStack, MappingBufferSource.itemBufferSource(bufferSource), null, combinedLight, combinedOverlay, ItemDisplayContext.FIXED.ordinal());

        poseStack.popPose();
    }

    public static double curve(double f, double offset) {
        return Math.sin((f + offset) / 0.25 * 2 * Math.PI);
    }
}
