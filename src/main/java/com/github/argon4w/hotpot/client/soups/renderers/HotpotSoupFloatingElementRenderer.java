package com.github.argon4w.hotpot.client.soups.renderers;

import com.github.argon4w.hotpot.client.soups.IHotpotSoupCustomElementRenderer;
import com.github.argon4w.hotpot.client.soups.IHotpotSoupCustomElementRendererSerializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Math;

public class HotpotSoupFloatingElementRenderer implements IHotpotSoupCustomElementRenderer {
    private final ResourceLocation element1ResourceLocation;
    private final ResourceLocation element2ResourceLocation;
    private final boolean shouldRenderInBowl;

    public HotpotSoupFloatingElementRenderer(ResourceLocation element1ResourceLocation, ResourceLocation element2ResourceLocation, boolean shouldRenderInBowl) {
        this.element1ResourceLocation = element1ResourceLocation;
        this.element2ResourceLocation = element2ResourceLocation;
        this.shouldRenderInBowl = shouldRenderInBowl;
    }

    @Override
    public void render(BlockEntityRendererProvider.Context context, int time, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float renderedWaterLevel) {
        float f = time / 20f / 5f;

        float part1Rotation = Math.sin(f * (float) Math.PI) * 1.6f;
        float part2Rotation = Math.sin((f + 1f) * (float) Math.PI) * 1.6f;

        float part1Position = Math.cos(f * (float) Math.PI) * 0.02f + renderedWaterLevel * 0.4375f + 0.5625f - 0.01f;
        float part2Position = Math.cos((f + 1f) * (float) Math.PI) * 0.02f + renderedWaterLevel * 0.4375f + 0.5625f - 0.01f;

        poseStack.pushPose();
        poseStack.translate(0f, part1Position, 0f);
        poseStack.mulPose(Axis.XP.rotationDegrees(part1Rotation));

        BakedModel part1Model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(element1ResourceLocation);
        context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.translucentCullBlockSheet()), null, part1Model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, Sheets.translucentCullBlockSheet());

        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0f, part2Position, 0f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(part2Rotation));

        BakedModel part2Model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(element2ResourceLocation);
        context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.translucentCullBlockSheet()), null, part2Model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, Sheets.translucentCullBlockSheet());

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderInBowl() {
        return shouldRenderInBowl;
    }

    public static class Serializer implements IHotpotSoupCustomElementRendererSerializer<HotpotSoupFloatingElementRenderer> {
        @Override
        public HotpotSoupFloatingElementRenderer fromJson(JsonObject jsonObject) {
            if (!jsonObject.has("element1_resource_location")) {
                throw new JsonParseException("Floating element renderer must have a \"element1_resource_location\"");
            }

            if (!ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(jsonObject, "element1_resource_location"))) {
                throw new JsonParseException("\"element1_resource_location\" in the floating element renderer must be a valid resource location");
            }

            if (!jsonObject.has("element2_resource_location")) {
                throw new JsonParseException("Floating element renderer must have a \"element2_resource_location\"");
            }

            if (!ResourceLocation.isValidResourceLocation(GsonHelper.getAsString(jsonObject, "element2_resource_location"))) {
                throw new JsonParseException("\"element2_resource_location\" in the floating element renderer must be a valid resource location");
            }

            if (!jsonObject.has("should_render_in_bowl")) {
                throw new JsonParseException("Floating element renderer must have a \"should_render_in_bowl\"");
            }

            ResourceLocation element1ResourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "element1_resource_location"));
            ResourceLocation element2ResourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "element2_resource_location"));
            boolean shouldRenderInBowl = GsonHelper.getAsBoolean(jsonObject, "should_render_in_bowl");

            return new HotpotSoupFloatingElementRenderer(element1ResourceLocation, element2ResourceLocation, shouldRenderInBowl);
        }
    }
}
