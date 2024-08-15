package com.github.argon4w.hotpot.client.items.sprites;

import com.github.argon4w.hotpot.client.items.sprites.colors.HotpotSpriteColorProviders;
import com.github.argon4w.hotpot.items.components.HotpotSpriteConfigDataComponent;
import com.github.argon4w.hotpot.items.sprites.IHotpotSpriteConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
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

    private class ProcessedOverrides extends ItemOverrides {
        @Nullable
        @Override
        public BakedModel resolve(@NotNull BakedModel bakedModel, @NotNull ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int seed) {
            BakedModel resolved = overlayModelMap.resolveOriginalModel(itemStack, clientLevel, livingEntity, seed);

            if (resolved == null) {
                return null;
            }

            if (!HotpotSpriteConfigDataComponent.hasDataComponent(itemStack)) {
                return resolved;
            }

            return new BakedModel() {
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
                    return Stream.concat(Stream.of(resolved), overlayModelMap.getResolvedTintedModels(itemStack, clientLevel, livingEntity, seed).stream()).map(bakedModel -> bakedModel.getRenderPasses(itemStack, fabulous)).flatMap(Collection::stream).toList();
                }

                @NotNull
                @Override
                public ItemTransforms getTransforms() {
                    return resolved.getTransforms();
                }
            };
        }
    }
}
