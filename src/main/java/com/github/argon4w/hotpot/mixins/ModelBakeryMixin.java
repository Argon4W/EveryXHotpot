package com.github.argon4w.hotpot.mixins;

import com.github.argon4w.hotpot.items.CheesedBakedModel;
import com.github.argon4w.hotpot.items.SimpleModelBaker;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Mixin(ModelBakery.class)
public class ModelBakeryMixin {
    @Shadow
    private @Final Map<ResourceLocation, BakedModel> bakedTopLevelModels;
    @Shadow
    private @Final Map<ResourceLocation, UnbakedModel> topLevelModels;
    @Shadow
    public @Final static BlockModel GENERATION_MARKER;
    @Shadow
    public @Final static ModelResourceLocation MISSING_MODEL_LOCATION;

    @Inject(method = "bakeModels", at = @At("RETURN"))
    public void bakeModels(BiFunction<ResourceLocation, Material, TextureAtlasSprite> atlasSpriteGetter, CallbackInfo ci) {
        topLevelModels.forEach(((location, unbakedModel) -> {
            if (unbakedModel instanceof BlockModel blockModel && blockModel.getRootModel() == GENERATION_MARKER) {
                ResourceLocation cheesedLocation = location instanceof ModelResourceLocation modelLocation ?
                        new ModelResourceLocation(location.withSuffix("_cheesed"), modelLocation.getVariant())
                        : location.withSuffix("_cheesed");

                Function<Material, TextureAtlasSprite> spriteGetter = material -> atlasSpriteGetter.apply(cheesedLocation, new Material(
                        material.atlasLocation(),
                        material.texture().withSuffix("_cheesed")
                ));

                SimpleModelBaker baker = new SimpleModelBaker(topLevelModels, topLevelModels.get(MISSING_MODEL_LOCATION), spriteGetter);

                BakedModel itemModel = baker.bake(
                        cheesedLocation,
                        blockModel,
                        BlockModelRotation.X0_Y0,
                        spriteGetter
                );

                bakedTopLevelModels.put(location, new CheesedBakedModel(bakedTopLevelModels.get(location), itemModel));
            }
        }));
    }
}
