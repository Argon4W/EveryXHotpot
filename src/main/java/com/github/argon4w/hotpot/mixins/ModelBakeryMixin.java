package com.github.argon4w.hotpot.mixins;

import com.github.argon4w.hotpot.client.items.process.HotpotSpriteProcessors;
import com.github.argon4w.hotpot.client.items.process.IHotpotSpriteProcessor;
import com.github.argon4w.hotpot.client.items.process.ProcessedBakedModel;
import com.github.argon4w.hotpot.client.items.process.SimpleModelBaker;
import com.github.argon4w.hotpot.client.items.process.processors.HotpotEmptySpriteProcessor;
import com.google.common.collect.Maps;
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

    @Shadow @Final private UnbakedModel missingModel;

    @Inject(method = "bakeModels", at = @At("RETURN"))
    public void bakeModels(ModelBakery.TextureGetter atlasSpriteGetter, CallbackInfo ci) {
        topLevelModels.forEach(((location, unbakedModel) -> {
            if (unbakedModel instanceof BlockModel blockModel && blockModel.getRootModel() == GENERATION_MARKER) {
                HashMap<ResourceLocation, BakedModel> processedModels = Maps.newHashMap();

                for (IHotpotSpriteProcessor processor : HotpotSpriteProcessors.getSpriteProcessorRegistry()) {
                    if (processor instanceof HotpotEmptySpriteProcessor) {
                        continue;
                    }

                    SimpleModelBaker baker = new SimpleModelBaker(bakedTopLevelModels, unbakedCache, missingModel, material -> atlasSpriteGetter.get(location, material), processor);
                    processedModels.put(processor.getResourceLocation(), baker.bakeUncached(unbakedModel, BlockModelRotation.X0_Y0));
                }

                bakedTopLevelModels.put(location, new ProcessedBakedModel(bakedTopLevelModels.get(location), processedModels));
            }
        }));
    }
}
