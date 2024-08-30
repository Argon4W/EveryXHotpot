package com.github.argon4w.hotpot.client.items.sprites;

import com.github.argon4w.hotpot.items.components.HotpotSpriteConfigDataComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
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

@SuppressWarnings("deprecation")
public record OverlayBakedModel(OverlayModelMap overlayModelMap) implements BakedModel {
    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, @NotNull RandomSource randomSource) {
        return overlayModelMap.getOriginalModel().getQuads(state, direction, randomSource);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return overlayModelMap.getOriginalModel().useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return overlayModelMap.getOriginalModel().isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return overlayModelMap.getOriginalModel().usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return overlayModelMap.getOriginalModel().isCustomRenderer();
    }

    @NotNull
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return overlayModelMap.getOriginalModel().getParticleIcon();
    }

    @NotNull
    @Override
    public ItemOverrides getOverrides() {
        return new ProcessedOverrides();
    }

    @NotNull
    @Override
    public ItemTransforms getTransforms() {
        return overlayModelMap.getOriginalModel().getTransforms();
    }

    @NotNull
    @Override
    public List<BakedModel> getRenderPasses(@NotNull ItemStack itemStack, boolean fabulous) {
        return overlayModelMap.getOriginalModel().getRenderPasses(itemStack, fabulous);
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
        return overlayModelMap.getOriginalModel().getQuads(state, side, rand, data, renderType);
    }

    @NotNull
    @Override
    public TriState useAmbientOcclusion(@NotNull BlockState state, @NotNull ModelData data, @NotNull RenderType renderType) {
        return overlayModelMap.getOriginalModel().useAmbientOcclusion(state, data, renderType);
    }

    @NotNull
    @Override
    public BakedModel applyTransform(@NotNull ItemDisplayContext transformType, @NotNull PoseStack poseStack, boolean applyLeftHandTransform) {
        return new OverlayBakedModel(overlayModelMap.applyTransform(transformType, poseStack, applyLeftHandTransform));
    }

    @NotNull
    @Override
    public ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        return overlayModelMap.getOriginalModel().getModelData(level, pos, state, modelData);
    }

    @NotNull
    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
        return overlayModelMap.getOriginalModel().getParticleIcon();
    }

    @NotNull
    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        return overlayModelMap.getOriginalModel().getRenderTypes(state, rand, data);
    }

    @NotNull
    @Override
    public List<RenderType> getRenderTypes(@NotNull ItemStack itemStack, boolean fabulous) {
        return overlayModelMap.getOriginalModel().getRenderTypes(itemStack, fabulous);
    }

    private class ProcessedOverrides extends ItemOverrides {
        @Nullable
        @Override
        public BakedModel resolve(@NotNull BakedModel bakedModel, @NotNull ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int seed) {
            BakedModel resolved = overlayModelMap.resolveOriginalModel(itemStack, clientLevel, livingEntity, seed);

            if (resolved == overlayModelMap.getOriginalModel() && OverlayBakedModel.this != bakedModel) {
                resolved = bakedModel;
            }

            if (resolved == null) {
                return null;
            }

            if (!HotpotSpriteConfigDataComponent.hasDataComponent(itemStack)) {
                return resolved;
            }

            return new TintedResolvedBakedModel(resolved, overlayModelMap.getResolvedTintedModels(itemStack, clientLevel, livingEntity, seed));
        }
    }
}
