package com.github.argon4w.hotpot.client.items.process;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public record SimpleModelBaker(Map<ModelResourceLocation, BakedModel> bakedModels, Map<ResourceLocation, UnbakedModel> models, UnbakedModel missingModel, Function<Material, TextureAtlasSprite> spriteGetter, IHotpotSpriteProcessor processor) implements ModelBaker {
    @NotNull
    @Override
    public UnbakedModel getModel(ResourceLocation location) {
        return models.getOrDefault(location, missingModel);
    }

    @Nullable
    @Override
    public BakedModel bake(ResourceLocation location, ModelState modelState) {
        return bake(location, modelState, getModelTextureGetter());
    }

    @Override
    public @Nullable UnbakedModel getTopLevelModel(ModelResourceLocation location) {
        return models.getOrDefault(location, missingModel);
    }

    @Nullable
    @Override
    public BakedModel bake(ResourceLocation location, ModelState state, Function<Material, TextureAtlasSprite> spriteGetter) {
        return bakeUncached(getModel(location), state, spriteGetter);
    }

    @Nullable
    public  BakedModel bakeUncached(UnbakedModel model, ModelState modelState) {
        return bakeUncached(model, modelState, getModelTextureGetter());
    }

    @Nullable
    @Override
    public  BakedModel bakeUncached(UnbakedModel model, ModelState state, Function<Material, TextureAtlasSprite> sprites) {
        return model instanceof BlockModel blockModel ? new ProcessedItemModelGenerator(processor).generateBlockModel(spriteGetter, blockModel).bake(
                this,
                blockModel,
                spriteGetter,
                BlockModelRotation.X0_Y0,
                false
        ) : model.bake(this, spriteGetter, state);
    }

    @Override
    @NotNull
    public Function<Material, TextureAtlasSprite> getModelTextureGetter() {
        return spriteGetter;
    }
}
