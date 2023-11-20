package com.github.argon4w.hotpot.blocks;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.client.model.data.EmptyModelData;

public class HotpotBlockEntityRenderer extends TileEntityRenderer<HotpotBlockEntity> {
    public HotpotBlockEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void render(HotpotBlockEntity blockEntity, float partialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay) {
        float waterLevel = blockEntity.getWaterLevel();
        boolean isVanillaBufferSource = bufferSource instanceof IRenderTypeBuffer.Impl; //Fix crashes when using Rubidium

        float renderedWaterLevel = blockEntity.renderedWaterLevel;
        float difference = (waterLevel - renderedWaterLevel);
        blockEntity.renderedWaterLevel = (renderedWaterLevel < 0) ? waterLevel : ((difference < 0.02f) ? waterLevel : renderedWaterLevel + difference * partialTick / 8f);
        blockEntity.renderedWaterLevel = Math.max(0.35f, blockEntity.renderedWaterLevel);

        for (int i = 0; i < blockEntity.getContents().size(); i++) {
            blockEntity.getContents().get(i).render(renderer, blockEntity, poseStack, bufferSource, combinedLight, combinedOverlay, 0.125f * i, renderedWaterLevel);
        }

        //Fix crashes when using Rubidium
        if (isVanillaBufferSource) {
            IRenderTypeBuffer.Impl source = (IRenderTypeBuffer.Impl) bufferSource;

            //FIXME: Probably UNSAFE FOR RENDERING!
            source.endBatch(RenderType.entitySolid(AtlasTexture.LOCATION_BLOCKS));
            source.endBatch(RenderType.entityCutout(AtlasTexture.LOCATION_BLOCKS));
            source.endBatch(RenderType.entityCutoutNoCull(AtlasTexture.LOCATION_BLOCKS));
            source.endBatch(RenderType.entitySmoothCutout(AtlasTexture.LOCATION_BLOCKS));
        }

        blockEntity.getSoup().getCustomElementRenderers().forEach(iHotpotSoupCustomElementRenderer -> iHotpotSoupCustomElementRenderer.render(renderer, blockEntity, partialTick, poseStack, bufferSource, combinedLight, combinedOverlay, renderedWaterLevel));

        blockEntity.getSoup().getSoupResourceLocation().ifPresent(soupLocation -> {
            poseStack.pushPose();
            poseStack.translate(0, Math.max(0.563f, renderedWaterLevel * 0.4375f + 0.5625f), 0);

            IBakedModel model = Minecraft.getInstance().getModelManager().getModel(soupLocation);
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.translucent()), null, model, 1, 1, 1, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);

            poseStack.popPose();
        });

        //Fix crashes when using Rubidium
        if (isVanillaBufferSource) {
            IRenderTypeBuffer.Impl source = (IRenderTypeBuffer.Impl) bufferSource;

            //FIXME: Probably UNSAFE FOR RENDERING!
            source.endBatch(Atlases.translucentCullBlockSheet());
            source.endBatch(RenderType.glintTranslucent());
        }
    }

    @Override
    public boolean shouldRenderOffScreen(HotpotBlockEntity hotpotBlockEntity) {
        return false;
    }
}
