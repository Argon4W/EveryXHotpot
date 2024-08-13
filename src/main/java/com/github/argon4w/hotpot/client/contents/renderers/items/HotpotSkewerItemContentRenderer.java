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
    public void render(AbstractHotpotItemStackContent itemStackContent, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float waterLevel, float rotation, float x, float z) {
        poseStack.pushPose();

        float positionX = 0.5f + x * 0.315f;
        float positionZ = 0.5f + z * 0.315f;
        float positionY = 0.56f - getFloatingCurve(rotation / 360.0f, 0.0f) * 0.03f + 0.42f * waterLevel;

        float degree = - ((float) Math.toDegrees(Math.safeAsin( (waterLevel - 0.35f) / (1.0f - 0.35f))) / 90.0f) * 85.0f;
        float rotationY = rotation + 20.0f;
        float rotationX = 270.0f + degree;

        poseStack.translate(positionX, positionY, positionZ);

        poseStack.mulPose(Axis.YP.rotationDegrees(rotationY));
        poseStack.mulPose(Axis.XN.rotationDegrees(rotationX));

        poseStack.scale(0.32f, 0.32f, 0.32f);

        Minecraft.getInstance().getItemRenderer().renderStatic(null, itemStackContent.getItemStack(), ItemDisplayContext.NONE, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemDisplayContext.NONE.ordinal());

        poseStack.popPose();
    }

    public float getFloatingCurve(float f, float offset) {
        return (float) Math.sin((f + offset) / 0.25f * 2f * Math.PI);
    }
}
