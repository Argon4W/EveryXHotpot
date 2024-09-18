package com.github.argon4w.hotpot.mixins;

import com.github.argon4w.hotpot.client.items.sprites.OverlayBakedModel;
import com.github.argon4w.hotpot.client.items.sprites.OverlayModelMap;
import com.github.argon4w.hotpot.client.items.sprites.SimpleModelBaker;
import com.github.argon4w.hotpot.client.items.sprites.processors.HotpotSpriteProcessors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Shadow
    private @Final Map<ModelResourceLocation, BakedModel> bakedTopLevelModels;
    @Shadow
    private @Final Map<ModelResourceLocation, UnbakedModel> topLevelModels;
    @Shadow
    public @Final static BlockModel GENERATION_MARKER;
    @Shadow
    private @Final Map<ResourceLocation, UnbakedModel> unbakedCache;

    @Shadow
    private @Final UnbakedModel missingModel;

    @Inject(method = "bakeModels", at = @At("RETURN"))
    public void bakeModels(ModelBakery.TextureGetter atlasSpriteGetter, CallbackInfo ci) {
        for (ModelResourceLocation modelResourceLocation : this.bakedTopLevelModels.keySet()) {
            UnbakedModel unbakedModel = this.topLevelModels.get(modelResourceLocation);

            if (!(unbakedModel instanceof BlockModel blockModel)) {
                continue;
            }

            if (blockModel.getRootModel() != GENERATION_MARKER) {
                continue;
            }

            if (blockModel.customData.getCustomGeometry() != null) {
                continue;
            }

            bakedTopLevelModels.put(modelResourceLocation, new OverlayBakedModel(HotpotSpriteProcessors.getSpriteProcessorRegistry().holders().collect(() -> new OverlayModelMap(bakedTopLevelModels.get(modelResourceLocation)), (map, reference) -> map.put(reference.key().location(), new SimpleModelBaker(bakedTopLevelModels, unbakedCache, missingModel, material -> atlasSpriteGetter.get(modelResourceLocation, material), reference.value()).bakeUncached(unbakedModel)), HashMap::putAll)));
        }
    }
}
