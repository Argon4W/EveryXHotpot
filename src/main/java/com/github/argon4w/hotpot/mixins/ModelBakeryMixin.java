package com.github.argon4w.hotpot.mixins;

import com.github.argon4w.hotpot.items.CheesedBakedModel;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.SpriteMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Shadow
    private @Final Map<ResourceLocation, IBakedModel> bakedTopLevelModels;
    @Shadow
    private @Final Map<ResourceLocation, IUnbakedModel> topLevelModels;
    @Shadow
    public @Final static BlockModel GENERATION_MARKER;

    @Shadow @Nullable private SpriteMap atlasSet;

    @Shadow(remap = false) @Nullable public abstract IBakedModel getBakedModel(ResourceLocation pLocation, IModelTransform pTransform, Function<RenderMaterial, TextureAtlasSprite> textureGetter);

    @Shadow @Final private static Logger LOGGER;

    @Shadow public abstract IUnbakedModel getModel(ResourceLocation p_209597_1_);

    @Shadow @Final private static ItemModelGenerator ITEM_MODEL_GENERATOR;

    @Inject(method = "uploadTextures", at = @At("RETURN"))
    public void uploadTextures(TextureManager pResourceManager, IProfiler pProfiler, CallbackInfoReturnable<SpriteMap> cir) {
        topLevelModels.forEach(((location, unbakedModel) -> {
            if (unbakedModel instanceof BlockModel && ((BlockModel) unbakedModel).getRootModel() == GENERATION_MARKER) {
                Function<RenderMaterial, TextureAtlasSprite> spriteGetter = material -> atlasSet.getSprite(new RenderMaterial(
                            material.atlasLocation(),
                            new ResourceLocation(material.texture().getNamespace(), material.texture().getPath().concat("_cheesed"))
                    ));

                IBakedModel bakedModel = null;

                try {
                    IUnbakedModel iunbakedmodel = getModel(location);
                    if (iunbakedmodel instanceof BlockModel) {
                        BlockModel blockmodel = (BlockModel)iunbakedmodel;
                        if (blockmodel.getRootModel() == GENERATION_MARKER) {
                            bakedModel = ITEM_MODEL_GENERATOR.generateBlockModel(spriteGetter, blockmodel).bake((ModelBakery) (Object) this, blockmodel, spriteGetter, ModelRotation.X0_Y0, location, false);
                        }
                    } else {
                        bakedModel = iunbakedmodel.bake((ModelBakery) (Object) this, spriteGetter, ModelRotation.X0_Y0, location);
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                    LOGGER.warn("Unable to bake model: '{}': {}", location, exception);
                }

                if (bakedModel != null) {
                    bakedTopLevelModels.put(location, new CheesedBakedModel(bakedTopLevelModels.get(location), bakedModel));
                }
            }
        }));
    }
}
