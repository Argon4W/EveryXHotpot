package com.github.argon4w.hotpot.client.items.process;

import com.github.argon4w.hotpot.items.components.HotpotSpriteProcessorDataComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("deprecation")
public record ProcessedBakedModel(BakedModel originalModel, HashMap<ResourceLocation, BakedModel> processedModels) implements BakedModel {
    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource randomSource) {
        return originalModel.getQuads(state, direction, randomSource);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return originalModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return originalModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return originalModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return originalModel.isCustomRenderer();
    }

    @NotNull
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return originalModel.getParticleIcon();
    }

    @NotNull
    @Override
    public ItemOverrides getOverrides() {
        return new ProcessedOverrides();
    }

    @NotNull
    @Override
    public ItemTransforms getTransforms() {
        return originalModel.getTransforms();
    }

    @NotNull
    @Override
    public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
        return originalModel.getRenderPasses(itemStack, fabulous);
    }

    private boolean isProcessed(ItemStack itemStack) {
        return !getVisibleProcessedModels(itemStack).isEmpty();
    }

    private List<ResourceLocation> getVisibleProcessedModels(ItemStack itemStack) {
        if (!HotpotSpriteProcessorDataComponent.hasDataComponent(itemStack)) {
            return List.of();
        }

        List<ResourceLocation> processors = HotpotSpriteProcessorDataComponent.getProcessors(itemStack).stream().map(IHotpotSpriteProcessor::getResourceLocation).toList();
        return processedModels.keySet().stream().filter(processors::contains).toList();
    }

    private class ProcessedOverrides extends ItemOverrides {
        @Nullable
        @Override
        public BakedModel resolve(BakedModel bakedModel, ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int p_173469_) {
            BakedModel resolved = originalModel.getOverrides().resolve(bakedModel, itemStack, clientLevel, livingEntity, p_173469_);

            if (!isProcessed(itemStack)) {
                return resolved;
            }

            if (resolved == null) {
                return null;
            }

            List<BakedModel> resolvedProcessedModels = getVisibleProcessedModels(itemStack).stream().map(processedModels::get).map(processedModel -> processedModel.getOverrides().resolve(processedModel, itemStack, clientLevel, livingEntity, p_173469_)).toList();

            return new BakedModel() {
                @Override
                public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
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

                @Override
                public TextureAtlasSprite getParticleIcon() {
                    return resolved.getParticleIcon();
                }

                @Override
                public ItemOverrides getOverrides() {
                    return resolved.getOverrides();
                }

                @Override
                public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
                    ArrayList<BakedModel> renderPasses = new ArrayList<>();
                    renderPasses.add(resolved);
                    renderPasses.addAll(resolvedProcessedModels);

                    return renderPasses;
                }

                @Override
                public ItemTransforms getTransforms() {
                    return resolved.getTransforms();
                }
            };
        }
    }
}
