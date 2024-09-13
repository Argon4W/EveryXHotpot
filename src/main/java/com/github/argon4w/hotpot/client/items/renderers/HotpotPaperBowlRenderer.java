package com.github.argon4w.hotpot.client.items.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.blocks.HotpotBlockEntityRenderer;
import com.github.argon4w.hotpot.client.items.IHotpotItemSpecialRenderer;
import com.github.argon4w.hotpot.client.soups.HotpotSoupRendererConfig;
import com.github.argon4w.hotpot.client.soups.HotpotSoupRendererConfigManager;
import com.github.argon4w.hotpot.items.HotpotPaperBowlItem;
import com.github.argon4w.hotpot.soups.HotpotSoupStatus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Math;

import java.util.List;
import java.util.Optional;

public class HotpotPaperBowlRenderer implements IHotpotItemSpecialRenderer {
    @Override
    public void render(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (HotpotPaperBowlItem.isPaperBowlEmpty(itemStack)) {
            return;
        }

        HotpotSoupRendererConfig soupRendererConfig = HotpotSoupRendererConfigManager.getSoupRendererConfig(HotpotPaperBowlItem.getPaperBowlSoupType(itemStack).getKey());

        List<ItemStack> bowlItems = HotpotPaperBowlItem.getPaperBowlItems(itemStack);
        List<ItemStack> bowlSkewers = HotpotPaperBowlItem.getPaperBowlSkewers(itemStack);

        int size = bowlItems.size() + bowlSkewers.size();
        boolean drained = HotpotPaperBowlItem.getPaperBowlSoupStatus(itemStack) == HotpotSoupStatus.DRAINED;

        if (size > 8) {
            return;
        }

        double soupScale = 9.0 / 16.0;
        double soupOffset = 3.5 / 16.0;
        double soupPivot = soupOffset / soupScale;

        double fullWaterLevel = 0.46;
        double minWaterLevelNoLimit = fullWaterLevel * 0.4375 - 0.06 * 8.0;

        double minWaterLevel = fullWaterLevel * 0.4375 - 0.06 * 6.0 + 0.5625 + 0.04;
        double minElementLevel = (fullWaterLevel - (0.06 / 0.4375) * 6.0) + 0.13;

        double waterLevel = minWaterLevelNoLimit + 0.06 * size + 0.5625;
        double elementLevel = (fullWaterLevel - (0.06 / 0.4375) * 8.0) + (0.06 / 0.4375) * size;

        if (drained) {
            waterLevel = minWaterLevel;
            elementLevel = minElementLevel;
        }

        RandomSource randomSource = RandomSource.create();
        randomSource.setSeed(42);

        poseStack.pushPose();

        poseStack.scale((float) soupScale, (float) soupScale, (float) soupScale);
        poseStack.translate(soupPivot, 0, soupPivot);

        poseStack.pushPose();

        for (int i = 0; i < Math.min(4, bowlSkewers.size()); i ++) {
            double positionZ = 0.215 + i * 0.19;
            int skewerIndex = bowlSkewers.size() - i - 1;
            ItemStack skewerItemStack = bowlSkewers.get(skewerIndex);

            poseStack.pushPose();

            poseStack.translate(0.05, 1.0, positionZ);

            poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));

            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
            poseStack.mulPose(Axis.XP.rotationDegrees(50.0f));
            poseStack.mulPose(Axis.YP.rotationDegrees(30.0f));

            poseStack.scale(0.72f, 0.72f, 0.72f);

            Minecraft.getInstance().getItemRenderer().renderStatic(null, skewerItemStack, ItemDisplayContext.NONE, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemDisplayContext.NONE.ordinal());

            poseStack.popPose();
        }

        for (int i = 4; i < bowlSkewers.size(); i ++) {
            double positionZ = 0.29 + (i - 4) * 0.15;
            int skewerIndex = bowlSkewers.size() - i - 1;
            ItemStack skewerItemStack = bowlSkewers.get(skewerIndex);

            poseStack.pushPose();

            poseStack.translate(0.2, 1.02, positionZ);

            poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));

            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
            poseStack.mulPose(Axis.XP.rotationDegrees(50.0f));
            poseStack.mulPose(Axis.YP.rotationDegrees(-30.0f));

            poseStack.scale(0.72f, 0.72f, 0.72f);

            Minecraft.getInstance().getItemRenderer().renderStatic(null, skewerItemStack, ItemDisplayContext.NONE, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemDisplayContext.NONE.ordinal());

            poseStack.popPose();
        }

        for (int i = 0; i < bowlItems.size(); i ++) {
            boolean even = (i % 2) == 0;
            boolean even2 = ((i - i % 2) / 2) % 2 == 0;

            double yMovement = 0.06 * (i + bowlSkewers.size());
            double zMovement = even ? 0.05 : -0.05;

            double rotationX = even ? 90.0 : 80.0;
            double rotationY = even2 ? 90.0 : 0.0;

            Axis rotationAxisX = even ? Axis.XN : Axis.XP;

            int contentIndex = bowlItems.size() - i - 1;
            ItemStack bowlItemStack = bowlItems.get(contentIndex);

            poseStack.pushPose();

            poseStack.translate(0.5, 0.38 + 0.04, 0.5);
            poseStack.translate(0.0, yMovement, 0.0);

            poseStack.mulPose(Axis.YP.rotationDegrees((float) rotationY));
            poseStack.translate(0.0 , 0.0, zMovement);
            poseStack.mulPose(rotationAxisX.rotationDegrees((float) rotationX));

            //poseStack.scale(0.88f, 0.88f, 0.88f);
            poseStack.scale(0.98f, 0.98f, 0.98f);

            Minecraft.getInstance().getItemRenderer().renderStatic(null, bowlItemStack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemDisplayContext.FIXED.ordinal());

            poseStack.popPose();
        }

        HotpotBlockEntityRenderer.renderHotpotSoupCustomElements(soupRendererConfig, poseStack, bufferSource, 0, 50, combinedLight, combinedOverlay, Math.max(minElementLevel, elementLevel), true);
        HotpotBlockEntityRenderer.renderHotpotSoup(soupRendererConfig, poseStack, bufferSource, combinedLight, combinedOverlay, Math.max(minWaterLevel, waterLevel));
        HotpotBlockEntityRenderer.renderHotpotSoup(soupRendererConfig, poseStack, bufferSource, combinedLight, combinedOverlay, Math.max(minWaterLevel, waterLevel));

        poseStack.popPose();

        poseStack.popPose();
    }

    @Override
    public Optional<ResourceLocation> getDefaultItemModelResourceLocation() {
        return Optional.of(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "item/hotpot_paper_bowl_model"));
    }
}
