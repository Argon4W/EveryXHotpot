package com.github.argon4w.hotpot.items;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.client.model.ElementsModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public record SimpleCheesedModelBaker(Map<ResourceLocation, BakedModel> bakedModels, Map<ResourceLocation, UnbakedModel> models, UnbakedModel missingModel, Function<Material, TextureAtlasSprite> spriteGetter) implements ModelBaker {
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

    @Nullable
    @Override
    public BakedModel bake(ResourceLocation location, ModelState state, Function<Material, TextureAtlasSprite> spriteGetter) {
        UnbakedModel model = getModel(location);

        return bake(location, model, state, spriteGetter);
    }

    public BakedModel bake(ResourceLocation location, UnbakedModel model, ModelState state, Function<Material, TextureAtlasSprite> spriteGetter) {
        if (model instanceof BlockModel blockModel) {
            return new ItemModelGenerator().generateBlockModel(spriteGetter, blockModel).bake(
                    this,
                    blockModel,
                    spriteGetter,
                    BlockModelRotation.X0_Y0,
                    getCheesedLocation(location),
                    false
            );
        } else {
            return model.bake(this, spriteGetter, state, getCheesedLocation(location));
        }
    }

    @Override
    @NotNull
    public Function<Material, TextureAtlasSprite> getModelTextureGetter() {
        return spriteGetter;
    }

    public static ResourceLocation getCheesedLocation(ResourceLocation location) {
        return location instanceof ModelResourceLocation modelLocation ?
                new ModelResourceLocation(location.withSuffix("_cheesed"), modelLocation.getVariant())
                : location.withSuffix("_cheesed");
    }
}
