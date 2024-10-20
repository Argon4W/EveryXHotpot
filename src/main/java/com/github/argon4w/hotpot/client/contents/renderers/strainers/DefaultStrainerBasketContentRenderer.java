package com.github.argon4w.hotpot.client.contents.renderers.strainers;

import com.github.argon4w.hotpot.api.client.items.IHotpotStrainerBasketContentRenderer;
import com.github.argon4w.hotpot.client.MappingBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Math;

import java.util.List;

public class DefaultStrainerBasketContentRenderer implements IHotpotStrainerBasketContentRenderer {
    @Override
    public void renderInSoup(List<ItemStack> itemStacks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, int contentIndex, double waterLevel, double maxHeight, double time) {
        double fullHeight = waterLevel * maxHeight;
        double safeZoneHeight = fullHeight * 0.75;
        double safeZonePositionY = (fullHeight - safeZoneHeight) / 2.0;
        double safeZoneOffsetY = safeZoneHeight / itemStacks.size();

        for (int i = 0; i < Math.min(4, itemStacks.size()); i++) {
            poseStack.pushPose();

            double curveOffset = (contentIndex + i + 1) * Math.PI;
            double positionOffset = curve(time, curveOffset);
            double rotationOffset = curve(time / 2.0, curveOffset);
            double positionY = safeZonePositionY + safeZoneHeight - i * safeZoneOffsetY + positionOffset * safeZonePositionY;
            double rotationY = (i % 2) * 90.0f;
            double rotation = 15.0 * rotationOffset;
            Axis rotationAxis = (i % 2 == 0 ? Axis.ZP : Axis.XN);

            poseStack.translate(0.0, positionY, 0.0);
            poseStack.mulPose(rotationAxis.rotationDegrees((float) rotation));
            poseStack.mulPose(Axis.YP.rotationDegrees((float) rotationY));
            poseStack.mulPose(Axis.XN.rotationDegrees(90.0f));
            poseStack.scale(0.56f, 0.56f, 0.56f);

            Minecraft.getInstance().getItemRenderer().renderStatic(itemStacks.get(i), ItemDisplayContext.NONE, combinedLight, combinedOverlay, poseStack, MappingBufferSource.itemBufferSource(bufferSource), null, 42);

            poseStack.popPose();
        }
    }

    @Override
    public void renderAsItem(List<ItemStack> itemStacks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        double scale = 0.57;
        double scaleZ = scale * 2.4625;
        double offsetY = Math.sin(Math.toRadians(15.0)) * (4.5 / 16.0) + Math.sin(Math.toRadians(90.0 - 15.0)) * (scaleZ / 16.0);

        for (int i = 0; i < Math.min(4, itemStacks.size()); i++) {
            poseStack.pushPose();

            double rotationY = (i % 2) * 90.0f;
            double positionY = (i + 1 + 0.01) * offsetY;
            Axis rotationAxis = (i % 2 == 0 ? Axis.ZP : Axis.XN);

            poseStack.translate(0.0, positionY, 0.0);
            poseStack.mulPose(rotationAxis.rotationDegrees(15.0f));
            poseStack.mulPose(Axis.YP.rotationDegrees((float) rotationY));
            poseStack.mulPose(Axis.XN.rotationDegrees(90.0f));
            poseStack.scale((float) scale, (float) scale, (float) scaleZ);

            Minecraft.getInstance().getItemRenderer().renderStatic(itemStacks.get(i), ItemDisplayContext.NONE, combinedLight, combinedOverlay, poseStack, MappingBufferSource.itemBufferSource(bufferSource), null, 42);

            poseStack.popPose();
        }
    }

    public static double curve(double f, double offset) {
        return Math.sin((f + offset) * 8.0 * Math.PI);
    }
}
