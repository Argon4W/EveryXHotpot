package com.github.argon4w.hotpot.client.items.sprites;

import com.github.argon4w.hotpot.api.client.items.sprites.processors.IHotpotSpriteProcessor;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record SimpleModelBaker(
        Map<ModelResourceLocation, BakedModel> bakedModels,
        Map<ResourceLocation, UnbakedModel> unbakedCache,
        Map<ModelResourceLocation, UnbakedModel> topLevelModels,
        UnbakedModel missingModel,
        Function<Material, TextureAtlasSprite> spriteGetter,
        IHotpotSpriteProcessor processor)
        implements ModelBaker {
    public static final HashSet<ResourceLocation> VALID_PROCESSED_SPRITES = new HashSet<>();

    @NotNull @Override
    public UnbakedModel getModel(@NotNull ResourceLocation location) {
        return unbakedCache.getOrDefault(location, missingModel);
    }

    @Nullable @Override
    public BakedModel bake(@NotNull ResourceLocation location, @NotNull ModelState modelState) {
        return bake(location, modelState, getModelTextureGetter());
    }

    @Override
    public @Nullable UnbakedModel getTopLevelModel(@NotNull ModelResourceLocation location) {
        return topLevelModels.getOrDefault(location, missingModel);
    }

    @Nullable @Override
    public BakedModel bake(
            @NotNull ResourceLocation location, ModelState state, Function<Material, TextureAtlasSprite> spriteGetter) {
        return bakeUncached(getModel(location), state, spriteGetter);
    }

    @Nullable @Override
    public BakedModel bakeUncached(
            UnbakedModel model, ModelState state, Function<Material, TextureAtlasSprite> sprites) {
        return model instanceof BlockModel blockModel
                ? new ItemModelGenerator()
                        .generateBlockModel(getModelTextureGetter(), blockModel)
                        .bake(this, blockModel, getModelTextureGetter(), BlockModelRotation.X0_Y0, false)
                : model.bake(this, getModelTextureGetter(), state);
    }

    @Nullable public BakedModel bakeUncached(UnbakedModel model, ModelState modelState) {
        return bakeUncached(model, modelState, getModelTextureGetter());
    }

    @Nullable public BakedModel bakeUncached(UnbakedModel model) {
        return bakeUncached(model, BlockModelRotation.X0_Y0);
    }

    @Override
    @NotNull public Function<Material, TextureAtlasSprite> getModelTextureGetter() {
        return this::getModelTexture;
    }

    public TextureAtlasSprite getModelTexture(Material material) {
        return VALID_PROCESSED_SPRITES.contains(material.texture().withSuffix(processor.getSuffix()))
                ? spriteGetter.apply(new Material(
                        material.atlasLocation(), material.texture().withSuffix(processor.getSuffix())))
                : spriteGetter.apply(material);
    }
}
