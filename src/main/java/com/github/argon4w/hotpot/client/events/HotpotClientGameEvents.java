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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.lighting.LightPipelineAwareModelBlockRenderer;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.NotNull;
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

                if (!(Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity) instanceof ISectionGeometryBLockEntityRenderer<?> renderer)) {
                    continue;
                }

                context.getPoseStack().pushPose();
                context.getPoseStack().translate(pos.getX() - startPos.getX(), pos.getY() - startPos.getY(), pos.getZ() - startPos.getZ());

                try {
                    renderer.renderSectionGeometry(cast(blockEntity), context, new PoseStack(), pos, new SectionGeometryModelRenderer(RANDOM_SOURCE, context, pos));
                } catch (ClassCastException ignored) {

                }

                context.getPoseStack().popPose();
            }
        });
    }

    @SuppressWarnings("UnstableApiUsage")
    public record SectionGeometryModelRenderer(RandomSource randomSource, AddSectionGeometryEvent.SectionRenderingContext context, BlockPos pos) implements ISectionGeometryBLockEntityRenderer.ModelRenderer {
        @Override
        public void renderModel(BakedModel model, PoseStack stack, RenderType renderType, int overlay, ModelData modelData) {
            LightPipelineAwareModelBlockRenderer.render(context.getOrCreateChunkBuffer(renderType), context.getQuadLighter(true), context.getRegion(), new TransformedBakedModel(model, stack), context.getRegion().getBlockState(pos), pos, context.getPoseStack(), false, randomSource, 42L, overlay, modelData, renderType);
        }

        @Override
        public void renderModel(BakedModel model, BlockState blockState, PoseStack stack, RenderType renderType, int overlay, ModelData modelData) {
            LightPipelineAwareModelBlockRenderer.render(context.getOrCreateChunkBuffer(renderType), context.getQuadLighter(true), context.getRegion(), new TransformedBakedModel(model, stack), blockState, pos, context.getPoseStack(), false, randomSource, 42L, overlay, modelData, renderType);
        }

        @Override
        public void renderSimpleItem(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, int overlay) {
            renderSimpleItem(null, null, 0, displayContext, poseStack, overlay, itemStack);
        }

        @Override
        public void renderSimpleItem(Level level, LivingEntity entity, int seed, ItemDisplayContext displayContext, PoseStack poseStack, int overlay, ItemStack itemStack) {
            poseStack.pushPose();

            BakedModel model = ClientHooks.handleCameraTransforms(poseStack, Minecraft.getInstance().getItemRenderer().getModel(itemStack, level, entity, seed), displayContext, false);
            poseStack.translate(-0.5, -0.5, -0.5);

            model.getRenderPasses(itemStack, false).forEach(model1 -> renderModel(model1, poseStack, RenderType.translucent(), overlay, ModelData.EMPTY));

            poseStack.popPose();
        }
    }

    @SuppressWarnings("deprecation")
    public record TransformedBakedModel(BakedModel model, IQuadTransformer transformer) implements BakedModel {
        public TransformedBakedModel(BakedModel model, PoseStack poseStack) {
            this(model, QuadTransformers.applying(new Transformation(poseStack.last().pose())));
        }

        @NotNull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand) {
            return model.getQuads(state, side, rand).stream().map(transformer::process).toList();
        }

        @NotNull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
            return model.getQuads(state, side, rand, data, renderType).stream().map(transformer::process).toList();
        }

        @Override
        public boolean useAmbientOcclusion() {
            return model.useAmbientOcclusion();
        }

        @NotNull
        @Override
        public TriState useAmbientOcclusion(@NotNull BlockState state, @NotNull ModelData data, @NotNull RenderType renderType) {
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

        @NotNull
        @Override
        public TextureAtlasSprite getParticleIcon() {
            return model.getParticleIcon();
        }

        @NotNull
        @Override
        public TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
            return model.getParticleIcon(data);
        }

        @NotNull
        @Override
        public ItemOverrides getOverrides() {
            return model.getOverrides();
        }

        @NotNull
        @Override
        public ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
            return model.getModelData(level, pos, state, modelData);
        }

        @NotNull
        @Override
        public BakedModel applyTransform(@NotNull ItemDisplayContext transformType, @NotNull PoseStack poseStack, boolean applyLeftHandTransform) {
            return new TransformedBakedModel(model.applyTransform(transformType, poseStack, applyLeftHandTransform), transformer);
        }

        @NotNull
        @Override
        public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
            return model.getRenderTypes(state, rand, data);
        }

        @NotNull
        @Override
        public List<RenderType> getRenderTypes(@NotNull ItemStack itemStack, boolean fabulous) {
            return model.getRenderTypes(itemStack, fabulous);
        }

        @NotNull
        @Override
        public List<BakedModel> getRenderPasses(@NotNull ItemStack itemStack, boolean fabulous) {
            return model.getRenderPasses(itemStack, fabulous);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> T cast(BlockEntity o) {
        return (T) o;
    }
}