package com.github.argon4w.hotpot.client.blocks;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.client.contents.HotpotContentRenderers;
import com.github.argon4w.hotpot.client.soups.HotpotSoupRendererConfig;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Math;

public class HotpotBlockEntityRenderer implements BlockEntityRenderer<HotpotBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public HotpotBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }
    @Override

    public void render(HotpotBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        float waterLevel = blockEntity.getWaterLevel();

        float renderedWaterLevel = blockEntity.renderedWaterLevel;
        float difference = (waterLevel - renderedWaterLevel);

        float newRenderedWaterLevel = renderedWaterLevel + difference * partialTick / 8f;

        if (Math.abs(difference) < 0.02f) {
            newRenderedWaterLevel = waterLevel;
        }

        if (renderedWaterLevel < 0) {
            blockEntity.renderedWaterLevel = waterLevel;
        } else {
            blockEntity.renderedWaterLevel = newRenderedWaterLevel;
        }

        blockEntity.renderedWaterLevel = Math.max(0.35f, blockEntity.renderedWaterLevel);

        float interval = 360.0f / 8.0f;
        float round = blockEntity.getTime() / 20.0f / 60.0f * 360.0f;

        float lastOrbitX = orbitX(interval * 7.0f + round);
        float lastOrbitY = orbitY(interval * 7.0f + round);

        for (int i = 0; i < blockEntity.getContents().size(); i++) {
            float orbitX = orbitX(interval * i + round);
            float orbitY = orbitY(interval * i + round);

            float rotation = (float) Math.toDegrees(Math.atan2(lastOrbitY - orbitY, lastOrbitX - orbitX));

            lastOrbitX = orbitX;
            lastOrbitY = orbitY;

            IHotpotContent content = blockEntity.getContents().get(i);
            content.getContentFactoryHolder().unwrapKey().map(ResourceKey::location).ifPresent(key -> HotpotContentRenderers.getContentRenderer(key).render(content, context, poseStack, bufferSource, combinedLight, combinedOverlay, rotation, renderedWaterLevel, orbitY, orbitX));
        }

        //Make Oculus Happy
        poseStack.pushPose();
        context.getItemRenderer().render(ItemStack.EMPTY, ItemDisplayContext.FIXED, false, poseStack, bufferSource, combinedLight, combinedOverlay, null);
        poseStack.popPose();

        HotpotSoupRendererConfig soupRendererConfig = HotpotModEntry.HOTPOT_SOUP_RENDERER_CONFIG_MANAGER.getSoupRendererConfig(blockEntity.getSoup().getSoupTypeFactoryHolder().key());

        renderHotpotSoupCustomElements(soupRendererConfig, context, poseStack, bufferSource, blockEntity.getTime(), partialTick, combinedLight, combinedOverlay, renderedWaterLevel, false);
        renderHotpotSoup(soupRendererConfig, context, poseStack, bufferSource, combinedLight, combinedOverlay, Math.max(0.563f, renderedWaterLevel * 0.4375f + 0.5625f));
    }

    public static void renderHotpotSoup(HotpotSoupRendererConfig soupRendererConfig, BlockEntityRendererProvider.Context context, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float renderedWaterLevel) {
        soupRendererConfig.soupModelResourceLocation().ifPresent(soupLocation -> {
            poseStack.pushPose();
            poseStack.translate(0, renderedWaterLevel, 0);

            int lighting = combinedLight;

            if (soupRendererConfig.fixedLighting()) {
                lighting = 14680304;
            }

            BakedModel model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(ModelResourceLocation.standalone(soupLocation));
            context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.translucentCullBlockSheet()), null, model, 1, 1, 1, lighting, combinedOverlay, ModelData.EMPTY, RenderType.translucent());

            poseStack.popPose();
        });
    }

    public static void renderHotpotSoupCustomElements(HotpotSoupRendererConfig soupRendererConfig, BlockEntityRendererProvider.Context context, PoseStack poseStack, MultiBufferSource bufferSource, int time, float partialTick, int combinedLight, int combinedOverlay, float renderedWaterLevel, boolean bowlOnly) {
        soupRendererConfig.customElementRenderers().stream()
                .filter(renderer -> !bowlOnly || renderer.shouldRenderInBowl())
                .forEach(iHotpotSoupCustomElementRenderer -> iHotpotSoupCustomElementRenderer.render(context, time, partialTick, poseStack, bufferSource, combinedLight, combinedOverlay, renderedWaterLevel));
    }

    private float orbitX(float degree) {
        return Math.cos(Math.toRadians(degree)) * 0.4f + squareX(degree) * 0.6f;
    }

    private float orbitY(float degree) {
        return Math.sin(Math.toRadians(degree)) * 0.4f + squareY(degree) * 0.6f;
    }

    private float squareX(float degree) {
        degree = degree % 360;
        return switch ((int) ((degree - (degree % 45)) / 45)) {
            case 0, 7 -> 1.0f;
            case 1, 2 -> 1.0f / Math.tan(Math.toRadians(degree));
            case 3, 4 -> -1.0f;
            case 5, 6 -> -1.0f / Math.tan(Math.toRadians(degree));
            default -> Float.NaN;
        };
    }

    private float squareY(float degree) {
        degree = degree % 360;
        return switch ((int) ((degree - (degree % 45)) / 45)) {
            case 0, 7 -> Math.tan(Math.toRadians(degree));
            case 1, 2 -> 1.0f;
            case 3, 4 -> -Math.tan(Math.toRadians(degree));
            case 5, 6 -> -1.0f;
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
