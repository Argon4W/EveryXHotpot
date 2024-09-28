package com.github.argon4w.hotpot.client.blocks;

import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.client.contents.HotpotContentRenderers;
import com.github.argon4w.hotpot.client.soups.HotpotSoupRendererConfig;
import com.github.argon4w.hotpot.client.soups.HotpotSoupRendererConfigManager;
import com.github.argon4w.hotpot.api.contents.IHotpotContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Math;

public class HotpotBlockEntityRenderer implements BlockEntityRenderer<HotpotBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public HotpotBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(HotpotBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        double waterLevel = blockEntity.getSynchronizedWaterLevel();
        long clientTime = blockEntity.hasLevel() ? blockEntity.getLevel().getGameTime() : 0;

        double renderedWaterLevel = blockEntity.renderedWaterLevel;
        double difference = (waterLevel - renderedWaterLevel);

        double newRenderedWaterLevel = Math.abs(difference) < 0.02f ? waterLevel : (renderedWaterLevel + difference * partialTick / 8f);
        blockEntity.renderedWaterLevel = Math.max(0.35f, renderedWaterLevel < 0 ? waterLevel : newRenderedWaterLevel);

        double interval = 360.0f / 8.0f;
        double round = blockEntity.getTime() / 20.0f / 60.0f * 360.0f;

        double lastOrbitX = orbitX(interval * 7.0f + round);
        double lastOrbitY = orbitY(interval * 7.0f + round);

        for (int i = 0; i < blockEntity.getContents().size(); i++) {
            double orbitX = orbitX(interval * i + round);
            double orbitY = orbitY(interval * i + round);

            double rotation = Math.toDegrees(Math.atan2(lastOrbitY - orbitY, lastOrbitX - orbitX));

            lastOrbitX = orbitX;
            lastOrbitY = orbitY;

            IHotpotContent content = blockEntity.getContents().get(i);
            content.getContentSerializerHolder().unwrapKey().map(ResourceKey::location).ifPresent(key -> HotpotContentRenderers.getContentRenderer(key).render(content, poseStack, bufferSource, combinedLight, combinedOverlay, rotation, renderedWaterLevel, orbitY, orbitX));
        }

        HotpotSoupRendererConfig soupRendererConfig = HotpotSoupRendererConfigManager.getSoupRendererConfig(blockEntity.getSoup().soupTypeHolder().getKey());

        renderHotpotSoupCustomElements(soupRendererConfig, poseStack, bufferSource, clientTime, partialTick, combinedLight, combinedOverlay, renderedWaterLevel, false);
        renderHotpotSoup(soupRendererConfig, poseStack, bufferSource, combinedLight, combinedOverlay, Math.max(0.563, renderedWaterLevel * 0.4375 + 0.5625));
    }

    public static void renderHotpotSoup(ResourceLocation resourceLocation, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, double renderedWaterLevel) {
        poseStack.pushPose();
        poseStack.translate(0, renderedWaterLevel, 0);

        BakedModel model = Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(resourceLocation));
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.translucentCullBlockSheet()), null, model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, RenderType.translucent());

        poseStack.popPose();
    }

    public static void renderHotpotSoup(HotpotSoupRendererConfig soupRendererConfig, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, double renderedWaterLevel) {
        soupRendererConfig.soupModelResourceLocation().ifPresent(resourceLocation -> renderHotpotSoup(resourceLocation, poseStack, bufferSource, soupRendererConfig.fixedLighting() ? 14680304 : combinedLight, combinedOverlay, renderedWaterLevel));
    }

    public static void renderHotpotSoupCustomElements(HotpotSoupRendererConfig soupRendererConfig, PoseStack poseStack, MultiBufferSource bufferSource, long time, float partialTick, int combinedLight, int combinedOverlay, double renderedWaterLevel, boolean bowlOnly) {
        soupRendererConfig.customElementRenderers().stream().filter(renderer -> !bowlOnly || renderer.shouldRenderInBowl()).forEach(iHotpotSoupCustomElementRenderer -> iHotpotSoupCustomElementRenderer.render(time, partialTick, poseStack, bufferSource, combinedLight, combinedOverlay, renderedWaterLevel));
    }

    private double orbitX(double degree) {
        return Math.cos(Math.toRadians(degree)) * 0.4f + squareX(degree) * 0.6f;
    }

    private double orbitY(double degree) {
        return Math.sin(Math.toRadians(degree)) * 0.4f + squareY(degree) * 0.6f;
    }

    private double squareX(double degree) {
        degree = degree % 360;
        return switch ((int) ((degree - (degree % 45)) / 45)) {
            case 0, 7 -> 1.0;
            case 1, 2 -> 1.0 / Math.tan(Math.toRadians(degree));
            case 3, 4 -> -1.0;
            case 5, 6 -> -1.0 / Math.tan(Math.toRadians(degree));
            default -> Float.NaN;
        };
    }

    private double squareY(double degree) {
        degree = degree % 360;
        return switch ((int) ((degree - (degree % 45)) / 45)) {
            case 0, 7 -> Math.tan(Math.toRadians(degree));
            case 1, 2 -> 1.0;
            case 3, 4 -> -Math.tan(Math.toRadians(degree));
            case 5, 6 -> -1.0;
            default -> Float.NaN;
        };
    }

    @Override
    public boolean shouldRenderOffScreen(HotpotBlockEntity hotpotBlockEntity) {
        return false;
    }

    @Override
    public int getViewDistance() {
        return 24;
    }
}
