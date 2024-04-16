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
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.ModelData;

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
        blockEntity.renderedWaterLevel = (renderedWaterLevel < 0) ? waterLevel : ((difference < 0.02f) ? waterLevel : renderedWaterLevel + difference * partialTick / 8f);
        blockEntity.renderedWaterLevel = Math.max(0.35f, blockEntity.renderedWaterLevel);

        for (int i = 0; i < blockEntity.getContents().size(); i++) {
            IHotpotContent content = blockEntity.getContents().get(i);
            HotpotContentRenderers.getContentRenderer(content.getResourceLocation()).render(content, context, blockEntity, poseStack, bufferSource, combinedLight, combinedOverlay, 0.125f * i, renderedWaterLevel);
        }

        //Make Oculus Happy
        poseStack.pushPose();
        context.getItemRenderer().render(ItemStack.EMPTY, ItemDisplayContext.FIXED, false, poseStack, bufferSource, combinedLight, combinedOverlay, null);
        poseStack.popPose();

        renderHotpotSoup(context, poseStack, bufferSource, blockEntity.getSoup().getResourceLocation(), blockEntity.getTime(), partialTick, combinedLight, combinedOverlay, renderedWaterLevel, false, true);
    }

    @SuppressWarnings("deprecation")
    public static void renderHotpotSoup(BlockEntityRendererProvider.Context context, PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation soupResourceLocation, int time, float partialTick, int combinedLight, int combinedOverlay, float renderedWaterLevel, boolean bowl, boolean renderElements) {
        boolean isVanillaBufferSource = bufferSource instanceof MultiBufferSource.BufferSource; //Fix crashes when using Rubidium

        if (isVanillaBufferSource) {
            MultiBufferSource.BufferSource source = (MultiBufferSource.BufferSource) bufferSource;

            //FIXME: Probably UNSAFE FOR RENDERING!
            source.endBatch(RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS));
            source.endBatch(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));
            source.endBatch(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));
            source.endBatch(RenderType.entitySmoothCutout(TextureAtlas.LOCATION_BLOCKS));
        }

        HotpotSoupRendererConfig soupRendererConfig = HotpotModEntry.HOTPOT_SOUP_RENDERER_CONFIG_MANAGER.getSoupRendererConfig(soupResourceLocation);

        soupRendererConfig.getCustomElementRenderers().stream()
                .filter(renderer -> ((!bowl) || renderer.shouldRenderInBowl()) && renderElements)
                .forEach(iHotpotSoupCustomElementRenderer -> iHotpotSoupCustomElementRenderer.render(context, time, partialTick, poseStack, bufferSource, combinedLight, combinedOverlay, renderedWaterLevel));

        soupRendererConfig.getSoupModelResourceLocation().ifPresent(soupLocation -> {
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
