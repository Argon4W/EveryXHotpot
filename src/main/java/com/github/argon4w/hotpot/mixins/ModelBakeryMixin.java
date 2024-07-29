package com.github.argon4w.hotpot.mixins;

import com.github.argon4w.hotpot.client.items.process.HotpotSpriteProcessors;
import com.github.argon4w.hotpot.client.items.process.IHotpotSpriteProcessor;
import com.github.argon4w.hotpot.client.items.process.ProcessedBakedModel;
import com.github.argon4w.hotpot.client.items.process.SimpleModelBaker;
import com.github.argon4w.hotpot.client.items.process.processors.HotpotEmptySpriteProcessor;
import com.google.common.collect.Maps;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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

    @Shadow protected abstract void registerModelAndLoadDependencies(ModelResourceLocation p_352435_, UnbakedModel p_352250_);

    @Shadow abstract UnbakedModel getModel(ResourceLocation p_119342_);

    @Inject(method = "<init>", at = @At("TAIL"))
    public void constructor(BlockColors blockColors, ProfilerFiller pProfilerFiller, Map<ResourceLocation, BlockModel> modelResources, Map<ResourceLocation, List<BlockStateModelLoader.LoadedJson>> blockStateResources, CallbackInfo ci) {
        modelResources.keySet().stream().filter(resourceLocation -> resourceLocation.getPath().startsWith("models/soup")).filter(resourceLocation -> resourceLocation.getPath().endsWith(".json")).map(resourceLocation -> resourceLocation.withPath(string -> string.substring(7, string.length() - 5))).forEach(resourceLocation -> registerModelAndLoadDependencies(ModelResourceLocation.standalone(resourceLocation), getModel(resourceLocation)));
    }

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

            HashMap<ResourceLocation, BakedModel> processedModels = Maps.newHashMap();

            for (ResourceKey<IHotpotSpriteProcessor> key : HotpotSpriteProcessors.getSpriteProcessorRegistry().registryKeySet()) {
                IHotpotSpriteProcessor processor = HotpotSpriteProcessors.getSpriteProcessorRegistry().get(key);

                if (processor instanceof HotpotEmptySpriteProcessor) {
                    continue;
                }

                SimpleModelBaker baker = new SimpleModelBaker(bakedTopLevelModels, unbakedCache, missingModel, material -> atlasSpriteGetter.get(modelResourceLocation, material), processor);
                processedModels.put(key.location(), baker.bakeUncached(unbakedModel, BlockModelRotation.X0_Y0));
            }

            bakedTopLevelModels.put(modelResourceLocation, new ProcessedBakedModel(bakedTopLevelModels.get(modelResourceLocation), processedModels));
        }
    }
}
