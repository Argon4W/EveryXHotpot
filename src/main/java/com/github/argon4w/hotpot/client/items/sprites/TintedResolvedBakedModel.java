package com.github.argon4w.hotpot.client.items.sprites;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public record TintedResolvedBakedModel(BakedModel resolved, List<BakedModel> tintedModels) implements BakedModel {
    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, @NotNull RandomSource randomSource) {
        return resolved.getQuads(blockState, direction, randomSource);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return resolved.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return resolved.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return resolved.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return resolved.isCustomRenderer();
    }

    @NotNull
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return resolved.getParticleIcon();
    }

    @NotNull
    @Override
    public ItemOverrides getOverrides() {
        return resolved.getOverrides();
    }

    @NotNull
    @Override
    public List<BakedModel> getRenderPasses(@NotNull ItemStack itemStack, boolean fabulous) {
        return Stream.concat(Stream.of(resolved), tintedModels.stream()).map(bakedModel -> bakedModel.getRenderPasses(itemStack, fabulous)).flatMap(Collection::stream).toList();
    }

    @NotNull
    @Override
    public ItemTransforms getTransforms() {
        return resolved.getTransforms();
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
        return resolved.getQuads(state, side, rand, data, renderType);
    }

    @NotNull
    @Override
    public TriState useAmbientOcclusion(@NotNull BlockState state, @NotNull ModelData data, @NotNull RenderType renderType) {
        return resolved.useAmbientOcclusion(state, data, renderType);
    }

    @NotNull
    @Override
    public BakedModel applyTransform(@NotNull ItemDisplayContext transformType, @NotNull PoseStack poseStack, boolean applyLeftHandTransform) {
        return new TintedResolvedBakedModel(resolved.applyTransform(transformType, poseStack, applyLeftHandTransform), tintedModels);
    }

    @NotNull
    @Override
    public ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        return resolved.getModelData(level, pos, state, modelData);
    }

    @NotNull
    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
        return resolved.getParticleIcon();
    }

    @NotNull
    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        return resolved.getRenderTypes(state, rand, data);
    }

    @NotNull
    @Override
    public List<RenderType> getRenderTypes(@NotNull ItemStack itemStack, boolean fabulous) {
        return resolved.getRenderTypes(itemStack, fabulous);
    }
}
