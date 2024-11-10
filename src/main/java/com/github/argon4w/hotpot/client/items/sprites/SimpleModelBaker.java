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

public class SimpleModelBaker implements ModelBaker {

    public static final HashSet<ResourceLocation> VALID_PROCESSED_SPRITES = new HashSet<>();

    private final Map<ResourceLocation, UnbakedModel> unbakedCache;
    private final Map<ModelResourceLocation, UnbakedModel> topLevelModels;
    private final UnbakedModel missingModel;
    private final Function<Material, TextureAtlasSprite> spriteGetter;
    private final IHotpotSpriteProcessor processor;

    public SimpleModelBaker(
            Map<ResourceLocation, UnbakedModel> unbakedCache,
            Map<ModelResourceLocation, UnbakedModel> topLevelModels,
            UnbakedModel missingModel,
            Function<Material, TextureAtlasSprite> spriteGetter,
            IHotpotSpriteProcessor processor) {
        this.unbakedCache = unbakedCache;
        this.topLevelModels = topLevelModels;
        this.missingModel = missingModel;
        this.spriteGetter = spriteGetter;
        this.processor = processor;
    }

    @NotNull @Override
    public UnbakedModel getModel(@NotNull ResourceLocation location) {
        return unbakedCache.getOrDefault(location, missingModel);
    }

    @Nullable @Override
    public BakedModel bake(@NotNull ResourceLocation location, @NotNull ModelState modelState) {
        return bake(location, modelState, getModelTextureGetter());
    }

    @Nullable @Override
    public UnbakedModel getTopLevelModel(@NotNull ModelResourceLocation location) {
        return topLevelModels.getOrDefault(location, missingModel);
    }

    @Nullable
    public BakedModel bakeUncached(UnbakedModel model, ModelState modelState) {
        return bakeUncached(model, modelState, getModelTextureGetter());
    }

    @Nullable
    public BakedModel bakeUncached(UnbakedModel model) {
        return bakeUncached(model, BlockModelRotation.X0_Y0);
    }

    @NotNull @Override
    public Function<Material, TextureAtlasSprite> getModelTextureGetter() {
        return this::getModelTexture;
    }

    @Nullable @Override
    public BakedModel bake(
            @NotNull ResourceLocation location,
            @NotNull ModelState state,
            @NotNull Function<Material, TextureAtlasSprite> spriteGetter) {
        return bakeUncached(getModel(location), state, spriteGetter);
    }

    @Nullable @Override
    public BakedModel bakeUncached(
            @NotNull UnbakedModel model,
            @NotNull ModelState state,
            @NotNull Function<Material, TextureAtlasSprite> sprites) {
        return model instanceof BlockModel blockModel
                ? new ItemModelGenerator().generateBlockModel(getModelTextureGetter(), blockModel).bake(this, blockModel, getModelTextureGetter(), BlockModelRotation.X0_Y0, false)
                : model.bake(this, getModelTextureGetter(), state);
    }

    public TextureAtlasSprite getModelTexture(Material material) {
        return VALID_PROCESSED_SPRITES.contains(material.texture().withSuffix(processor.getSuffix()))
                ? spriteGetter.apply(new Material(material.atlasLocation(), material.texture().withSuffix(processor.getSuffix())))
                : spriteGetter.apply(material);
    }
}
