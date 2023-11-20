package com.github.argon4w.hotpot.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.ModelData;

public class HotpotBlockEntityRenderer implements BlockEntityRenderer<HotpotBlockEntity> {
    private final BlockEntityRendererProvider.Context context;


    public HotpotBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }
    @Override
    @SuppressWarnings("deprecation")
    public void render(HotpotBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        float waterLevel = blockEntity.getWaterLevel();
        boolean isVanillaBufferSource = bufferSource instanceof MultiBufferSource.BufferSource; //Fix crashes when using Rubidium

        float renderedWaterLevel = blockEntity.renderedWaterLevel;
        float difference = (waterLevel - renderedWaterLevel);
        blockEntity.renderedWaterLevel = (renderedWaterLevel < 0) ? waterLevel : ((difference < 0.02f) ? waterLevel : renderedWaterLevel + difference * partialTick / 8f);
        blockEntity.renderedWaterLevel = Math.max(0.35f, blockEntity.renderedWaterLevel);

        for (int i = 0; i < blockEntity.getContents().size(); i++) {
            blockEntity.getContents().get(i).render(context, blockEntity, poseStack, bufferSource, combinedLight, combinedOverlay, 0.125f * i, renderedWaterLevel);
        }

        //Fix crashes when using Rubidium
        if (isVanillaBufferSource) {
            MultiBufferSource.BufferSource source = (MultiBufferSource.BufferSource) bufferSource;

            //FIXME: Probably UNSAFE FOR RENDERING!
            source.endBatch(RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS));
            source.endBatch(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));
            source.endBatch(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));
            source.endBatch(RenderType.entitySmoothCutout(TextureAtlas.LOCATION_BLOCKS));
        }

        blockEntity.getSoup().getCustomElementRenderers().forEach(iHotpotSoupCustomElementRenderer -> iHotpotSoupCustomElementRenderer.render(context, blockEntity, partialTick, poseStack, bufferSource, combinedLight, combinedOverlay, renderedWaterLevel));

        blockEntity.getSoup().getSoupResourceLocation().ifPresent(soupLocation -> {
            poseStack.pushPose();
            poseStack.translate(0, Math.max(0.563f, renderedWaterLevel * 0.4375f + 0.5625f), 0);

            BakedModel model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(soupLocation);
            context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.translucent()), null, model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, RenderType.translucent());

            poseStack.popPose();
        });

        //Fix crashes when using Rubidium
        if (isVanillaBufferSource) {
            MultiBufferSource.BufferSource source = (MultiBufferSource.BufferSource) bufferSource;

            //FIXME: Probably UNSAFE FOR RENDERING!
            source.endBatch(Sheets.translucentCullBlockSheet());
            source.endBatch(RenderType.glintTranslucent());
        }
    }

    @Override
    public boolean shouldRenderOffScreen(HotpotBlockEntity hotpotBlockEntity) {
        return false;
    }

    @Override
    public int getViewDistance() {
        return 24;
    }

    public record Bubble(float x, float z, int offset, int startTime) {}
}
