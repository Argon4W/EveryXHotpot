package com.github.argon4w.hotpot.client.items.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.blocks.HotpotBlockEntityRenderer;
import com.github.argon4w.hotpot.client.items.IHotpotItemSpecialRenderer;
import com.github.argon4w.hotpot.client.soups.HotpotSoupRendererConfig;
import com.github.argon4w.hotpot.client.soups.HotpotSoupRendererConfigManager;
import com.github.argon4w.hotpot.items.HotpotPaperBowlItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
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
        Optional<ResourceLocation> soupResourceLocation = HotpotPaperBowlItem.getPaperBowlSoup(itemStack);

        if (soupResourceLocation.isEmpty()) {
            return;
        }

        HotpotSoupRendererConfig soupRendererConfig = HotpotModEntry.HOTPOT_SOUP_RENDERER_CONFIG_MANAGER.getSoupRendererConfig(soupResourceLocation.get());

        if (soupRendererConfig == HotpotSoupRendererConfigManager.EMPTY_SOUP_RENDER_CONFIG) {
            return;
        }

        List<ItemStack> bowlItems = HotpotPaperBowlItem.getPaperBowlItems(itemStack);
        List<ItemStack> bowlSkewers = HotpotPaperBowlItem.getPaperBowlSkewers(itemStack);

        int size = bowlItems.size() + bowlSkewers.size();
        boolean drained = HotpotPaperBowlItem.isPaperBowlDrained(itemStack);

        if (size > 8) {
            return;
        }

        BlockEntityRendererProvider.Context context = new BlockEntityRendererProvider.Context(
                Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getBlockRenderer(),
                Minecraft.getInstance().getItemRenderer(),
                Minecraft.getInstance().getEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels(),
                Minecraft.getInstance().font
        );

        float soupScale = 9.0f / 16.0f;
        float soupOffset = 3.5f / 16.0f;
        float soupPivot = soupOffset / soupScale;

        float fullWaterLevel = 0.46f;
        float minWaterLevelNoLimit = fullWaterLevel * 0.4375f - 0.06f * 8.0f;

        float minWaterLevel = fullWaterLevel * 0.4375f - 0.06f * 6.0f + 0.5625f + 0.04f;
        float minElementLevel = (fullWaterLevel - (0.06f / 0.4375f) * 6.0f) + 0.13f;

        float waterLevel = minWaterLevelNoLimit + 0.06f * size + 0.5625f;
        float elementLevel = (fullWaterLevel - (0.06f / 0.4375f) * 8.0f) + (0.06f / 0.4375f) * size;

        if (drained) {
            waterLevel = minWaterLevel;
            elementLevel = minElementLevel;
        }

        RandomSource randomSource = RandomSource.create();
        randomSource.setSeed(42);

        poseStack.pushPose();

        poseStack.scale(soupScale, soupScale, soupScale);
        poseStack.translate(soupPivot, 0, soupPivot);

        poseStack.pushPose();

        for (int i = 0; i < Math.min(4, bowlSkewers.size()); i ++) {
            int skewerIndex = bowlSkewers.size() - i - 1;
            ItemStack skewerItemStack = bowlSkewers.get(skewerIndex);

            poseStack.pushPose();

            poseStack.translate(0.05f, 1.0f, 0.215f + i * 0.19f);

            poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));

            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
            poseStack.mulPose(Axis.XP.rotationDegrees(50.0f));
            poseStack.mulPose(Axis.YP.rotationDegrees(30.0f));

            poseStack.scale(0.72f, 0.72f, 0.72f);

            Minecraft.getInstance().getItemRenderer().renderStatic(null, skewerItemStack, ItemDisplayContext.NONE, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemDisplayContext.NONE.ordinal());

            poseStack.popPose();
        }

        for (int i = 4; i < bowlSkewers.size(); i ++) {
            int skewerIndex = bowlSkewers.size() - i - 1;
            ItemStack skewerItemStack = bowlSkewers.get(skewerIndex);

            poseStack.pushPose();

            poseStack.translate(0.2f, 1.02f, 0.29f + (i - 4) * 0.15f);

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

            float yMovement = 0.06f * (i + bowlSkewers.size());
            float zMovement = even ? 0.05f : -0.05f;

            float rotationX = even ? 90.0f : 80.0f;
            float rotationY = even2 ? 90.0f : 0.0f;

            Axis rotationAxisX = even ? Axis.XN : Axis.XP;

            int contentIndex = bowlItems.size() - i - 1;
            ItemStack bowlItemStack = bowlItems.get(contentIndex);

            poseStack.pushPose();

            poseStack.translate(0.5f, 0.38f + 0.04f, 0.5f);
            poseStack.translate(0.0f, yMovement, 0.0f);

            poseStack.mulPose(Axis.YP.rotationDegrees(rotationY));
            poseStack.translate(0.0f , 0.0f, zMovement);
            poseStack.mulPose(rotationAxisX.rotationDegrees(rotationX));

            poseStack.scale(0.88f, 0.88f, 0.88f);

            Minecraft.getInstance().getItemRenderer().renderStatic(null, bowlItemStack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemDisplayContext.FIXED.ordinal());

            poseStack.popPose();
        }

        HotpotBlockEntityRenderer.renderHotpotSoupCustomElements(
                soupRendererConfig,
                context,
                poseStack,
                bufferSource,
                0,
                50,
                combinedLight,
                combinedOverlay,
                Math.max(minElementLevel, elementLevel),
                true
        );

        HotpotBlockEntityRenderer.renderHotpotSoup(
                soupRendererConfig,
                context,
                poseStack,
                bufferSource,
                combinedLight,
                combinedOverlay,
                Math.max(minWaterLevel, waterLevel)
        );

        HotpotBlockEntityRenderer.renderHotpotSoup(
                soupRendererConfig,
                context,
                poseStack,
                bufferSource,
                combinedLight,
                combinedOverlay,
                Math.max(minWaterLevel, waterLevel)
        );

        poseStack.popPose();

        poseStack.popPose();
    }

    @Override
    public Optional<ResourceLocation> getDefaultItemModelResourceLocation() {
        return Optional.of(new ResourceLocation(HotpotModEntry.MODID, "item/hotpot_paper_bowl_model"));
    }
}
