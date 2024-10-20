package com.github.argon4w.hotpot.client.sections;

import com.github.argon4w.hotpot.api.client.sections.cache.RendererBakedModelsCache;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
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
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.lighting.LightPipelineAwareModelBlockRenderer;
import net.neoforged.neoforge.client.model.lighting.QuadLighter;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

/**
 * @author Argon4W
 */
@SuppressWarnings("UnstableApiUsage")
public record LightAwareSectionGeometryRenderContext(RandomSource randomSource, AddSectionGeometryEvent.SectionRenderingContext context, RendererBakedModelsCache cache, BlockPos pos, BlockPos regionOrigin) implements ISectionGeometryRenderContext {
    public LightAwareSectionGeometryRenderContext(AddSectionGeometryEvent.SectionRenderingContext context, RendererBakedModelsCache cache, BlockPos pos, BlockPos regionOrigin) {
        this(RandomSource.createNewThreadLocalInstance(), context, cache, pos, regionOrigin);
    }

    @Override
    public void renderCachedModel(BakedModel model, PoseStack poseStack, RenderType renderType, int overlay, ModelData modelData) {
        renderCachedModel(model, context.getRegion().getBlockState(pos), poseStack, renderType, overlay, modelData);
    }

    @Override
    public void renderCachedModel(BakedModel model, BlockState blockState, PoseStack poseStack, RenderType renderType, int overlay, ModelData modelData) {
        LightPipelineAwareModelBlockRenderer.render(context.getOrCreateChunkBuffer(renderType), context.getQuadLighter(true), context.getRegion(), cache.getTransformedModel(model, poseStack), blockState, pos, context.getPoseStack(), false, randomSource, 42L, overlay, modelData, renderType);
    }

    @Override
    public void renderUncachedItem(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, int overlay) {
        renderUncachedItem(null, null, 42, itemStack, displayContext, leftHand, poseStack, overlay);
    }

    @Override
    public void renderUncachedItem(Level level, LivingEntity entity, int seed, ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, int overlay) {
        Minecraft.getInstance().getItemRenderer().render(itemStack, displayContext, leftHand, poseStack, getUncachedItemBufferSource(), getPackedLight(), overlay, Minecraft.getInstance().getItemRenderer().getModel(itemStack, level, entity, seed));
    }

    @Override
    public int getPackedLight() {
        return LightTexture.pack(context.getRegion().getBrightness(LightLayer.BLOCK, pos), context.getRegion().getBrightness(LightLayer.SKY, pos));
    }

    @Override
    public MultiBufferSource getUncachedBufferSource() {
        return renderType -> new QuadLighterVertexConsumer(context.getOrCreateChunkBuffer(renderType));
    }

    @Override
    public MultiBufferSource getUncachedItemBufferSource() {
        return ModList.get().isLoaded("sodium") ?  pRenderType -> new QuadLighterVertexConsumer() : renderType -> new VanillaEntityVertexConsumer();
    }

    public class VanillaEntityVertexConsumer implements VertexConsumer {
        private final VertexConsumer vertexConsumer;

        public VanillaEntityVertexConsumer() {
            this.vertexConsumer = context.getOrCreateChunkBuffer(Sheets.translucentItemSheet());
        }

        @NotNull
        @Override
        public VertexConsumer addVertex(float pX, float pY, float pZ) {
            return vertexConsumer.addVertex(pX, pY, pZ);
        }

        @NotNull
        @Override
        public VertexConsumer setColor(int pRed, int pGreen, int pBlue, int pAlpha) {
            return vertexConsumer.setColor(pRed, pGreen, pBlue, pAlpha);
        }

        @NotNull
        @Override
        public VertexConsumer setUv(float pU, float pV) {
            return vertexConsumer.setUv(pU, pV);
        }

        @NotNull
        @Override
        public VertexConsumer setUv1(int pU, int pV) {
            return vertexConsumer.setUv1(pU, pV);
        }

        @NotNull
        @Override
        public VertexConsumer setUv2(int pU, int pV) {
            return vertexConsumer.setUv2(pU, pV);
        }

        @NotNull
        @Override
        public VertexConsumer setNormal(float pNormalX, float pNormalY, float pNormalZ) {
            return vertexConsumer.setNormal(pNormalX, pNormalY, pNormalZ);
        }

        @Override
        public void putBulkData(@NotNull PoseStack.Pose pose, @NotNull BakedQuad bakedQuad, float red, float green, float blue, float alpha, int packedLight, int packedOverlay) {
            putBulkData(pose, bakedQuad, red, green, blue, alpha, packedLight, packedOverlay, true);
        }

        @Override
        public void putBulkData(@NotNull PoseStack.Pose pose, @NotNull BakedQuad bakedQuad, float red, float green, float blue, float alpha, int packedLight, int packedOverlay, boolean readExistingColor) {
            IQuadTransformer transformer = QuadTransformers.applying(new Transformation(new Matrix4f().translate(regionOrigin.getX(), regionOrigin.getY(), regionOrigin.getZ()).mul(pose.pose())));
            vertexConsumer.putBulkData(context.getPoseStack().last(), transformer.process(bakedQuad), new float[] { 1.0F, 1.0F, 1.0F, 1.0F }, red, green, blue, alpha, new int[] { packedLight, packedLight, packedLight, packedLight }, packedOverlay, readExistingColor);
        }
    }

    public class QuadLighterVertexConsumer implements VertexConsumer {
        private final VertexConsumer vertexConsumer;

        public QuadLighterVertexConsumer(VertexConsumer vertexConsumer) {
            this.vertexConsumer = vertexConsumer;
        }

        public QuadLighterVertexConsumer() {
            this.vertexConsumer = context.getOrCreateChunkBuffer(RenderType.translucent());
        }

        @NotNull
        @Override
        public VertexConsumer addVertex(float pX, float pY, float pZ) {
            return vertexConsumer.addVertex(pX, pY, pZ);
        }

        @NotNull
        @Override
        public VertexConsumer setColor(int pRed, int pGreen, int pBlue, int pAlpha) {
            return vertexConsumer.setColor(pRed, pGreen, pBlue, pAlpha);
        }

        @NotNull
        @Override
        public VertexConsumer setUv(float pU, float pV) {
            return vertexConsumer.setUv(pU, pV);
        }

        @NotNull
        @Override
        public VertexConsumer setUv1(int pU, int pV) {
            return vertexConsumer.setUv1(pU, pV);
        }

        @NotNull
        @Override
        public VertexConsumer setUv2(int pU, int pV) {
            return vertexConsumer.setUv2(pU, pV);
        }

        @NotNull
        @Override
        public VertexConsumer setNormal(float pNormalX, float pNormalY, float pNormalZ) {
            return vertexConsumer.setNormal(pNormalX, pNormalY, pNormalZ);
        }

        @Override
        public void putBulkData(@NotNull PoseStack.Pose pose, @NotNull BakedQuad bakedQuad, float red, float green, float blue, float alpha, int packedLight, int packedOverlay) {
            putBulkData(pose, bakedQuad, red, green, blue, alpha, packedLight, packedOverlay, true);
        }

        @Override
        public void putBulkData(@NotNull PoseStack.Pose pose, @NotNull BakedQuad bakedQuad, float red, float green, float blue, float alpha, int packedLight, int packedOverlay, boolean readExistingColor) {
            IQuadTransformer transformer = QuadTransformers.applying(new Transformation(pose.pose()));
            QuadLighter lighter = context.getQuadLighter(false);
            BakedQuad quad = transformer.process(bakedQuad);

            lighter.setup(context.getRegion(), pos, context.getRegion().getBlockState(pos));
            lighter.computeLightingForQuad(quad);

            vertexConsumer.putBulkData(context.getPoseStack().last(), quad, lighter.getComputedBrightness(), red, green, blue, alpha, lighter.getComputedLightmap(), packedOverlay, readExistingColor);
            lighter.reset();
        }
    }
}