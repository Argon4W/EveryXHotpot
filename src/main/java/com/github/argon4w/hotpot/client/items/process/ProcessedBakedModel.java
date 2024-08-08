package com.github.argon4w.hotpot.client.items.process;

import com.github.argon4w.hotpot.client.HotpotColor;
import com.github.argon4w.hotpot.items.components.HotpotSpriteProcessorConfigDataComponent;
import com.github.argon4w.hotpot.items.process.IHotpotSpriteProcessorConfig;
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

import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class ProcessedBakedModel implements BakedModel {
    private final BakedModel originalModel;
    private final HashMap<ResourceLocation, BakedModel> processedModels;
    private final HashMap<ItemStackHolder, List<BakedModel>> cache;

    public ProcessedBakedModel(BakedModel originalModel, HashMap<ResourceLocation, BakedModel> processedModels) {
        this.originalModel = originalModel;
        this.processedModels = processedModels;
        this.cache = new HashMap<>();
    }

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

    private record ItemStackHolder(ItemStack itemStack) {
        @Override
        public boolean equals(Object obj) {
            return obj == this || (obj instanceof ItemStackHolder holder && ItemStack.isSameItemSameComponents(itemStack, holder.itemStack));
        }
    }

    private class ProcessedOverrides extends ItemOverrides {
        @Nullable
        @Override
        public BakedModel resolve(BakedModel bakedModel, ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int seed) {
            BakedModel resolved = originalModel.getOverrides().resolve(originalModel, itemStack, clientLevel, livingEntity, seed);

            if (resolved == null) {
                return null;
            }

            if (!HotpotSpriteProcessorConfigDataComponent.hasDataComponent(itemStack)) {
                return resolved;
            }

            List<IHotpotSpriteProcessorConfig> configs = HotpotSpriteProcessorConfigDataComponent.getProcessorConfigs(itemStack).stream().filter(config -> processedModels.containsKey(config.getProcessorResourceLocation())).toList();

            if (configs.isEmpty()) {
                return resolved;
            }

            List<BakedModel> resolvedProcessedModels = cache.computeIfAbsent(new ItemStackHolder(itemStack), holder -> configs.stream().<BakedModel>map(config -> {
                ResourceLocation processorResourceLocation = config.getProcessorResourceLocation();

                BakedModel processedModel = processedModels.get(processorResourceLocation);
                IHotpotSpriteProcessor spriteProcessor = HotpotSpriteProcessors.getSpriteProcessor(processorResourceLocation);

                BakedModel resolvedProcessedModel = processedModel.getOverrides().resolve(processedModel, itemStack, clientLevel, livingEntity, seed);
                HotpotColor color = spriteProcessor.getColor(config);

                return new TintedBakedModel(resolvedProcessedModel, color);
            }).toList());

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
                public List<BakedModel> getRenderPasses(ItemStack itemStack1, boolean fabulous) {
                    return Stream.concat(Stream.of(resolved), resolvedProcessedModels.stream()).toList();
                }

                @Override
                public ItemTransforms getTransforms() {
                    return resolved.getTransforms();
                }
            };
        }
    }
}
