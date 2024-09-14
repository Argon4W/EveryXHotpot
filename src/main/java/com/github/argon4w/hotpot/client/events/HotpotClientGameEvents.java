package com.github.argon4w.hotpot.client.events;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.blocks.ISectionGeometryBLockEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.OverlayTexture;
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
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.lighting.LightPipelineAwareModelBlockRenderer;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

//@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = HotpotModEntry.MODID, value = Dist.CLIENT)
public class HotpotClientGameEvents {
    public static final RandomSource RANDOM_SOURCE = RandomSource.createNewThreadLocalInstance();

    @SubscribeEvent
    public static void addSectionGeometry(AddSectionGeometryEvent event) {
        event.addRenderer(context -> {
            BlockAndTintGetter region = context.getRegion();
            BlockPos startPos = event.getSectionOrigin();
            BlockPos endPos = startPos.offset(15, 15, 15);

            for (BlockPos pos : BlockPos.betweenClosed(startPos, endPos)) {
                BlockEntity blockEntity = region.getBlockEntity(pos);

                if (blockEntity == null) {
                    continue;
                }

                if (!(Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity) instanceof ISectionGeometryBLockEntityRenderer renderer)) {
                    continue;
                }

                context.getPoseStack().pushPose();
                context.getPoseStack().translate(pos.getX() - startPos.getX(), pos.getY() - startPos.getY(), pos.getZ() - startPos.getZ());

                renderer.renderSectionGeometry(context, new PoseStack(), pos, (model, stack, renderType, overlay, modelData) -> LightPipelineAwareModelBlockRenderer.render(context.getOrCreateChunkBuffer(renderType), context.getQuadLighter(true), context.getRegion(), new RotatedBakedModel(model, stack), region.getBlockState(pos), pos, context.getPoseStack(), false, RANDOM_SOURCE, 42L, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType));
                context.getPoseStack().popPose();
            }
        });
    }

    public record RotatedBakedModel(BakedModel model, PoseStack poseStack) implements BakedModel {
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
            return model.getQuads(state, side, rand).stream().map(this::rotateQuad).toList();
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, @Nullable RenderType renderType) {
            return model.getQuads(state, side, rand, data, renderType).stream().map(this::rotateQuad).toList();
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
            return new RotatedBakedModel(model.applyTransform(transformType, poseStack, applyLeftHandTransform), poseStack);
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
            return model.getRenderPasses(itemStack, fabulous).stream().<BakedModel>map(model -> new RotatedBakedModel(model, poseStack)).toList();
        }

        public BakedQuad rotateQuad(BakedQuad bakedQuad) {
            int[] vertices = new int[bakedQuad.getVertices().length];
            System.arraycopy(bakedQuad.getVertices(), 0, vertices, 0, bakedQuad.getVertices().length);

            for (int i = 0; i < vertices.length / 8; i ++) {
                float x = Float.intBitsToFloat(vertices[i * 8]);
                float y = Float.intBitsToFloat(vertices[i * 8 + 1]);
                float z = Float.intBitsToFloat(vertices[i * 8 + 2]);

                Vector3f vector3f = poseStack.last().pose().transformPosition(x, y, z, new Vector3f());

                vertices[i * 8] = Float.floatToRawIntBits(vector3f.x);
                vertices[i * 8 + 1] = Float.floatToRawIntBits(vector3f.y);
                vertices[i * 8 + 2] = Float.floatToRawIntBits(vector3f.z);
            }

            Direction direction = FaceBakery.calculateFacing(vertices);
            net.neoforged.neoforge.client.ClientHooks.fillNormal(vertices, direction);

            return new BakedQuad(vertices, bakedQuad.getTintIndex(), direction, bakedQuad.getSprite(), bakedQuad.isShade(), bakedQuad.hasAmbientOcclusion());
        }
    };
}