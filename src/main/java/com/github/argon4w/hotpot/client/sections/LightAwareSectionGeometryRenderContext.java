package com.github.argon4w.hotpot.client.sections;

import com.github.argon4w.hotpot.api.client.sections.ISectionGeometryRenderContext;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.lighting.LightPipelineAwareModelBlockRenderer;
import net.neoforged.neoforge.client.model.lighting.QuadLighter;
import net.neoforged.neoforge.client.model.pipeline.TransformingVertexPipeline;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

/**
 * @author Argon4W
 */
@SuppressWarnings("UnstableApiUsage")
public class LightAwareSectionGeometryRenderContext implements ISectionGeometryRenderContext {
    public static final boolean SODIUM_LOADED = ModList.get().isLoaded("sodium");

    private final AddSectionGeometryEvent.SectionRenderingContext context;
    private final RendererBakedModelsCache cache;
    private final BlockPos pos;
    private final RandomSource randomSource;
    private final Transformation transformation;

    public LightAwareSectionGeometryRenderContext(AddSectionGeometryEvent.SectionRenderingContext context, RendererBakedModelsCache cache, BlockPos pos, BlockPos regionOrigin) {
        this.context = context;
        this.cache = cache;
        this.pos = pos;
        this.randomSource = RandomSource.createNewThreadLocalInstance();
        this.transformation = SODIUM_LOADED ? new Transformation(new Matrix4f(context.getPoseStack().last().pose())) : new Transformation(new Matrix4f(context.getPoseStack().last().pose()).translate(regionOrigin.getX(), regionOrigin.getY(), regionOrigin.getZ()));
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
        return renderType -> new QuadLighterVertexConsumer(context.getOrCreateChunkBuffer(renderType), context, pos);
    }

    @Override
    public MultiBufferSource getUncachedItemBufferSource() {
        return SODIUM_LOADED ? pRenderType -> new QuadLighterVertexConsumer(context, pos) : ignored -> new TransformingVertexPipeline(context.getOrCreateChunkBuffer(Sheets.translucentItemSheet()), transformation);
    }
}