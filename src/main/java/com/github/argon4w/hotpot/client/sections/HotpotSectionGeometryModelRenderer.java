package com.github.argon4w.hotpot.client.sections;

import com.github.argon4w.hotpot.client.blocks.IHotpotSectionGeometryBLockEntityRenderer;
import com.github.argon4w.hotpot.client.sections.cache.HotpotModelCache;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.lighting.LightPipelineAwareModelBlockRenderer;
import net.neoforged.neoforge.client.model.lighting.QuadLighter;

@SuppressWarnings("UnstableApiUsage")
public record HotpotSectionGeometryModelRenderer(RandomSource randomSource, AddSectionGeometryEvent.SectionRenderingContext context, HotpotModelCache cache, BlockPos pos) implements IHotpotSectionGeometryBLockEntityRenderer.ModelRenderer {
    public HotpotSectionGeometryModelRenderer(AddSectionGeometryEvent.SectionRenderingContext context, HotpotModelCache cache, BlockPos pos) {
        this(RandomSource.createNewThreadLocalInstance(), context, cache, pos);
    }

    @Override
    public void renderModel(BakedModel model, PoseStack poseStack, RenderType renderType, int overlay, ModelData modelData) {
        renderModel(model, context.getRegion().getBlockState(pos), poseStack, renderType, overlay, modelData);
    }

    @Override
    public void renderModel(BakedModel model, BlockState blockState, PoseStack poseStack, RenderType renderType, int overlay, ModelData modelData) {
        LightPipelineAwareModelBlockRenderer.render(context.getOrCreateChunkBuffer(renderType), context.getQuadLighter(true), context.getRegion(), cache.getTransformedModel(model, poseStack), blockState, pos, context.getPoseStack(), false, randomSource, 42L, overlay, modelData, renderType);
    }

    @Override
    public void renderItem(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, int overlay) {
        renderItem(null, null, 42, itemStack, displayContext, leftHand, poseStack, overlay);
    }

    @Override
    public void renderItem(Level level, LivingEntity entity, int seed, ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, int overlay) {
        Minecraft.getInstance().getItemRenderer().render(itemStack, displayContext, leftHand, poseStack, pRenderType -> new VertexConsumerWithQuadLighter(context.getOrCreateChunkBuffer(RenderType.cutout())), LightTexture.pack(context.getRegion().getBrightness(LightLayer.BLOCK, pos), context.getRegion().getBrightness(LightLayer.SKY, pos)), overlay, Minecraft.getInstance().getItemRenderer().getModel(itemStack, level, entity, seed));
    }

    public class VertexConsumerWithQuadLighter implements VertexConsumer {
        private final VertexConsumer vertexConsumer;

        public VertexConsumerWithQuadLighter(VertexConsumer vertexConsumer) {
            this.vertexConsumer = vertexConsumer;
        }

        @Override
        public VertexConsumer addVertex(float pX, float pY, float pZ) {
            return vertexConsumer.addVertex(pX, pY, pZ);
        }

        @Override
        public VertexConsumer setColor(int pRed, int pGreen, int pBlue, int pAlpha) {
            return vertexConsumer.setColor(pRed, pGreen, pBlue, pAlpha);
        }

        @Override
        public VertexConsumer setUv(float pU, float pV) {
            return vertexConsumer.setUv(pU, pV);
        }

        @Override
        public VertexConsumer setUv1(int pU, int pV) {
            return vertexConsumer.setUv1(pU, pV);
        }

        @Override
        public VertexConsumer setUv2(int pU, int pV) {
            return vertexConsumer.setUv2(pU, pV);
        }

        @Override
        public VertexConsumer setNormal(float pNormalX, float pNormalY, float pNormalZ) {
            return vertexConsumer.setNormal(pNormalX, pNormalY, pNormalZ);
        }

        @Override
        public void putBulkData(PoseStack.Pose pose, BakedQuad bakedQuad, float red, float green, float blue, float alpha, int packedLight, int packedOverlay, boolean readExistingColor) {
            QuadLighter lighter = context.getQuadLighter(false);
            IQuadTransformer transformer = QuadTransformers.applying(new Transformation(pose.pose()));

            lighter.setup(context.getRegion(), pos, context.getRegion().getBlockState(pos));
            lighter.computeLightingForQuad(bakedQuad);

            vertexConsumer.putBulkData(context.getPoseStack().last(), transformer.process(bakedQuad), lighter.getComputedBrightness(), red, green, blue, alpha, lighter.getComputedLightmap(), packedOverlay, true);
            lighter.reset();
        }
    }
}