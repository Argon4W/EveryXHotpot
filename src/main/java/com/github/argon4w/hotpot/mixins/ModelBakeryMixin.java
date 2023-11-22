package com.github.argon4w.hotpot.mixins;

import com.github.argon4w.hotpot.items.CheesedBakedModel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.AtlasSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Shadow
    private @Final Map<ResourceLocation, BakedModel> bakedTopLevelModels;
    @Shadow
    private @Final Map<ResourceLocation, UnbakedModel> topLevelModels;
    @Shadow
    public @Final static BlockModel GENERATION_MARKER;

    @Shadow @Nullable private AtlasSet atlasSet;

    @Shadow public abstract UnbakedModel getModel(ResourceLocation p_209597_1_);

    @Shadow @Final private static ItemModelGenerator ITEM_MODEL_GENERATOR;

    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "uploadTextures", at = @At("RETURN"))
    public void uploadTextures(TextureManager pResourceManager, ProfilerFiller p_119300_, CallbackInfoReturnable<AtlasSet> cir) {
        topLevelModels.forEach(((location, unbakedModel) -> {
            if (unbakedModel instanceof BlockModel && ((BlockModel) unbakedModel).getRootModel() == GENERATION_MARKER) {
                Function<Material, TextureAtlasSprite> spriteGetter = material -> atlasSet.getSprite(new Material(
                            material.atlasLocation(),
                            new ResourceLocation(material.texture().getNamespace(), material.texture().getPath().concat("_cheesed"))
                    ));

                BakedModel bakedModel = null;

                try {
                    UnbakedModel iunbakedmodel = getModel(location);
                    if (iunbakedmodel instanceof BlockModel) {
                        BlockModel blockmodel = (BlockModel)iunbakedmodel;
                        if (blockmodel.getRootModel() == GENERATION_MARKER) {
                            bakedModel = ITEM_MODEL_GENERATOR.generateBlockModel(spriteGetter, blockmodel).bake((ModelBakery) (Object) this, blockmodel, spriteGetter, BlockModelRotation.X0_Y0, location, false);
                        }
                    } else {
                        bakedModel = iunbakedmodel.bake((ModelBakery) (Object) this, spriteGetter, BlockModelRotation.X0_Y0, location);
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
