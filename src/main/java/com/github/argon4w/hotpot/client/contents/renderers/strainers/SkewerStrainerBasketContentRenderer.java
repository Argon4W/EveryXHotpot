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

public class SkewerStrainerBasketContentRenderer implements IHotpotStrainerBasketContentRenderer {
    @Override
    public void renderInSoup(List<ItemStack> itemStacks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, int contentIndex, double waterLevel, double maxHeight, double time) {
        double offsetX = (9 / 4.0) / 16.0;
        double startPositionX = offsetX * Math.max(0, itemStacks.size() - 1) / 2.0;
        double progress = (waterLevel - 0.35) / 0.65;

        for (int i = 0; i < Math.min(4, itemStacks.size()); i++) {
            poseStack.pushPose();

            double curveOffset = (contentIndex + i + 1) * Math.PI;
            double positionOffset = 0.5 + curve(time, curveOffset) * 0.5;
            double positionY = maxHeight + positionOffset * waterLevel * 0.15;

            double positionX = startPositionX - i * offsetX;
            double offsetSurfaceX = (4.5 / 16.0 - i * offsetX - positionX) * (1 - progress);
            double positionSurfaceX = positionX + offsetSurfaceX - (0.5 / 16.0) * (1 - progress);
            double rotationZ = 90.0 - Math.toDegrees(Math.atan2(positionY, offsetSurfaceX));

            double positionSurfaceZ = (i % 2 == 0 ? 0.1 : - 0.1) * (1 - progress);
            double rotationX = 90.0 - Math.toDegrees(java.lang.Math.atan2(positionY, positionSurfaceZ));

            poseStack.translate(positionSurfaceX, positionY, positionSurfaceZ);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) rotationX));
            poseStack.mulPose(Axis.ZN.rotationDegrees((float) rotationZ));
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.scale(0.68f, 0.68f, 0.68f);

            poseStack.pushPose();
            Minecraft.getInstance().getItemRenderer().renderStatic(itemStacks.get(i), ItemDisplayContext.NONE, combinedLight, combinedOverlay, poseStack, MappingBufferSource.itemBufferSource(bufferSource), null, 42);
            poseStack.popPose();

            poseStack.popPose();
        }
    }

    @Override
    public void renderAsItem(List<ItemStack> itemStacks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        double positionY = 0.98 - 0.01 / 0.45;
        double offsetX = (9 / 4.0) / 16.0;
        double startPositionX = offsetX * Math.max(0, itemStacks.size() - 1) / 2.0;

        for (int i = 0; i < Math.min(4, itemStacks.size()); i++) {
            poseStack.pushPose();

            double positionX = startPositionX - i * offsetX;
            double offsetSurfaceX = (4.5 / 16.0 - i * offsetX - positionX);
            double positionSurfaceX = positionX + offsetSurfaceX - (0.5 / 16.0);
            double rotationZ = 90.0 - Math.toDegrees(Math.atan2(positionY, offsetSurfaceX));

            double positionSurfaceZ = (i % 2 == 0 ? 0.1 : - 0.1);
            double rotationX = 90.0 - Math.toDegrees(java.lang.Math.atan2(positionY, positionSurfaceZ));

            poseStack.translate(positionSurfaceX, positionY, positionSurfaceZ);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) rotationX));
            poseStack.mulPose(Axis.ZN.rotationDegrees((float) rotationZ));
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.scale(0.68f, 0.68f, 0.68f);

            poseStack.pushPose();
            Minecraft.getInstance().getItemRenderer().renderStatic(itemStacks.get(i), ItemDisplayContext.NONE, combinedLight, combinedOverlay, poseStack, MappingBufferSource.itemBufferSource(bufferSource), null, 42);
            poseStack.popPose();

            poseStack.popPose();
        }
    }

    public static double curve(double f, double offset) {
        return Math.sin((f + offset) * 8.0 * Math.PI);
    }
}
