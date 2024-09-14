package com.github.argon4w.hotpot.client.events;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.blocks.ISectionGeometryBLockEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.lighting.LightPipelineAwareModelBlockRenderer;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = HotpotModEntry.MODID, value = Dist.CLIENT)
public class HotpotClientGameEvents {
    public static final RandomSource RANDOM_SOURCE = RandomSource.createNewThreadLocalInstance();

    @SubscribeEvent
    public static void addSectionGeometry(AddSectionGeometryEvent event) {
        event.addRenderer(context -> {
            BlockPos startPos = event.getSectionOrigin();
            BlockPos endPos = startPos.offset(15, 15, 15);

            for (BlockPos pos : BlockPos.betweenClosed(startPos, endPos)) {
                BlockEntity blockEntity = context.getRegion().getBlockEntity(pos);

                if (blockEntity == null) {
                    continue;
                }

                if (!(Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity) instanceof ISectionGeometryBLockEntityRenderer renderer)) {
                    continue;
                }

                context.getPoseStack().pushPose();
                context.getPoseStack().translate(pos.getX() - startPos.getX(), pos.getY() - startPos.getY(), pos.getZ() - startPos.getZ());

                renderer.renderSectionGeometry(context, new PoseStack(), pos, new SectionGeometryModelRenderer(context, pos));
                context.getPoseStack().popPose();
            }
        });
    }

    @SuppressWarnings("UnstableApiUsage")
    public record SectionGeometryModelRenderer(AddSectionGeometryEvent.SectionRenderingContext context, BlockPos pos) implements ISectionGeometryBLockEntityRenderer.ModelRenderer {
        @Override
        public void renderModel(BakedModel model, PoseStack stack, RenderType renderType, int overlay, ModelData modelData) {
            LightPipelineAwareModelBlockRenderer.render(context.getOrCreateChunkBuffer(renderType), context.getQuadLighter(true), context.getRegion(), new TransformedBakedModel(model, stack), context.getRegion().getBlockState(pos), pos, context.getPoseStack(), false, RANDOM_SOURCE, 42L, overlay, modelData, renderType);
        }

        @Override
        public void renderModel(BakedModel model, BlockState blockState, PoseStack stack, RenderType renderType, int overlay, ModelData modelData) {
            LightPipelineAwareModelBlockRenderer.render(context.getOrCreateChunkBuffer(renderType), context.getQuadLighter(true), context.getRegion(), new TransformedBakedModel(model, stack), blockState, pos, context.getPoseStack(), false, RANDOM_SOURCE, 42L, overlay, modelData, renderType);
        }
    }

    public record TransformedBakedModel(BakedModel model, IQuadTransformer transformer) implements BakedModel {
        public TransformedBakedModel(BakedModel model, PoseStack poseStack) {
            this(model, QuadTransformers.applying(new Transformation(poseStack.last().pose())));
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
            return model.getQuads(state, side, rand).stream().map(transformer::process).toList();
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, @Nullable RenderType renderType) {
            return model.getQuads(state, side, rand, data, renderType).stream().map(transformer::process).toList();
        }

        @Override
        public boolean useAmbientOcclusion() {
            return model.useAmbientOcclusion();
        }

        @Override
        public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType) {
            return model.useAmbientOcclusion(state, data, renderType);
        }

        @Override
        public boolean isGui3d() {
            return model.isGui3d();
        }

        @Override
        public boolean usesBlockLight() {
            return model.usesBlockLight();
        }

        @Override
        public boolean isCustomRenderer() {
            return model.isCustomRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return model.getParticleIcon();
        }

        @Override
        public TextureAtlasSprite getParticleIcon(ModelData data) {
            return model.getParticleIcon(data);
        }

        @Override
        public ItemOverrides getOverrides() {
            return model.getOverrides();
        }

        @Override
        public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
            return model.getModelData(level, pos, state, modelData);
        }

        @Override
        public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
            return new TransformedBakedModel(model.applyTransform(transformType, poseStack, applyLeftHandTransform), transformer);
        }

        @Override
        public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
            return model.getRenderTypes(state, rand, data);
        }

        @Override
        public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
            return model.getRenderTypes(itemStack, fabulous);
        }

        @Override
        public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
            return model.getRenderPasses(itemStack, fabulous).stream().<BakedModel>map(model -> new TransformedBakedModel(model, transformer)).toList();
        }
    };
}