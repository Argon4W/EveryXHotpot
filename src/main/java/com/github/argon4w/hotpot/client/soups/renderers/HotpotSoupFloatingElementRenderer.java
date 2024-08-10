package com.github.argon4w.hotpot.client.soups.renderers;

import com.github.argon4w.hotpot.client.soups.HotpotSoupCustomElements;
import com.github.argon4w.hotpot.client.soups.IHotpotSoupCustomElementRenderer;
import com.github.argon4w.hotpot.client.soups.IHotpotSoupCustomElementRendererSerializer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Math;

import java.util.List;

public class HotpotSoupFloatingElementRenderer implements IHotpotSoupCustomElementRenderer {
    private final ResourceLocation element1ModelResourceLocation;
    private final ResourceLocation element2ModelResourceLocation;
    private final boolean shouldRenderInBowl;

    public HotpotSoupFloatingElementRenderer(ResourceLocation element1ModelResourceLocation, ResourceLocation element2ModelResourceLocation, boolean shouldRenderInBowl) {
        this.element1ModelResourceLocation = element1ModelResourceLocation;
        this.element2ModelResourceLocation = element2ModelResourceLocation;
        this.shouldRenderInBowl = shouldRenderInBowl;
    }

    @Override
    public void render(BlockEntityRendererProvider.Context context, long time, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float renderedWaterLevel) {
        float f = time / 20f / 5f;

        float part1Rotation = Math.sin(f * (float) Math.PI) * 1.6f;
        float part2Rotation = Math.sin((f + 1f) * (float) Math.PI) * 1.6f;

        float part1Position = Math.cos(f * (float) Math.PI) * 0.02f + renderedWaterLevel * 0.4375f + 0.5625f - 0.01f;
        float part2Position = Math.cos((f + 1f) * (float) Math.PI) * 0.02f + renderedWaterLevel * 0.4375f + 0.5625f - 0.01f;

        poseStack.pushPose();
        poseStack.translate(0f, part1Position, 0f);
        poseStack.mulPose(Axis.XP.rotationDegrees(part1Rotation));

        BakedModel part1Model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(ModelResourceLocation.standalone(element1ModelResourceLocation));
        context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.translucentCullBlockSheet()), null, part1Model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, Sheets.translucentCullBlockSheet());

        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0f, part2Position, 0f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(part2Rotation));

        BakedModel part2Model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(ModelResourceLocation.standalone(element2ModelResourceLocation));
        context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.translucentCullBlockSheet()), null, part2Model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, Sheets.translucentCullBlockSheet());

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderInBowl() {
        return shouldRenderInBowl;
    }

    @Override
    public List<ResourceLocation> getRequiredModelResourceLocations() {
        return List.of(element1ModelResourceLocation, element2ModelResourceLocation);
    }

    @Override
    public IHotpotSoupCustomElementRendererSerializer<?> getSerializer() {
        return HotpotSoupCustomElements.HOTPOT_FLOATING_ELEMENT_RENDERER_SERIALIZER.get();
    }

    public ResourceLocation getElement1ModelResourceLocation() {
        return element1ModelResourceLocation;
    }

    public ResourceLocation getElement2ModelResourceLocation() {
        return element2ModelResourceLocation;
    }

    public static class Serializer implements IHotpotSoupCustomElementRendererSerializer<HotpotSoupFloatingElementRenderer> {
        public static final MapCodec<HotpotSoupFloatingElementRenderer> CODEC = RecordCodecBuilder.mapCodec(renderer -> renderer.group(
                ResourceLocation.CODEC.fieldOf("element1_model_resource_location").forGetter(HotpotSoupFloatingElementRenderer::getElement1ModelResourceLocation),
                ResourceLocation.CODEC.fieldOf("element2_model_resource_location").forGetter(HotpotSoupFloatingElementRenderer::getElement2ModelResourceLocation),
                Codec.BOOL.fieldOf("should_render_in_bowl").forGetter(HotpotSoupFloatingElementRenderer::shouldRenderInBowl)
        ).apply(renderer, HotpotSoupFloatingElementRenderer::new));

        @Override
        public MapCodec<HotpotSoupFloatingElementRenderer> getCodec() {
            return CODEC;
        }
    }
}
