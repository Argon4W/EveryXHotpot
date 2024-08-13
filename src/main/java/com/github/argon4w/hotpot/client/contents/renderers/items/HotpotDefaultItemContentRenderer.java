package com.github.argon4w.hotpot.client.contents.renderers.items;

import com.github.argon4w.hotpot.client.contents.IHotpotItemContentSpecialRenderer;
import com.github.argon4w.hotpot.contents.AbstractHotpotItemStackContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Math;

public class HotpotDefaultItemContentRenderer implements IHotpotItemContentSpecialRenderer {
    @Override
    public void render(AbstractHotpotItemStackContent itemStackContent, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float waterLevel, float rotation, float x, float z) {
        poseStack.pushPose();

        float positionX = 0.5f + x * 0.315f;
        float positionZ = 0.5f + z * 0.315f;
        float positionY = 0.53f - getFloatingCurve(rotation / 360.0f, 0.0f) * 0.06f + 0.42f * waterLevel;

        float rotationY = rotation - 90.0f;
        float rotationX = getFloatingCurve(rotation / 360.0f, 1f) * 25.0f;

        poseStack.translate(positionX, positionY, positionZ);

        poseStack.mulPose(Axis.YP.rotationDegrees(rotationY));
        poseStack.mulPose(Axis.XP.rotationDegrees( 90.0f));
        poseStack.mulPose(Axis.XP.rotationDegrees(rotationX));

        poseStack.scale(0.25f, 0.25f, 0.25f);

        Minecraft.getInstance().getItemRenderer().renderStatic(null, itemStackContent.getItemStack(), ItemDisplayContext.FIXED, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemDisplayContext.FIXED.ordinal());

        poseStack.popPose();
    }

    public static float getFloatingCurve(float f, float offset) {
        return (float) Math.sin((f + offset) / 0.25f * 2f * Math.PI);
    }
}
