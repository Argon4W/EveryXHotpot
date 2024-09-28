package com.github.argon4w.hotpot.client.sections.cache;

import com.github.argon4w.hotpot.api.client.sections.cache.IBakedModelCache;
import com.github.argon4w.hotpot.api.client.sections.cache.RendererBakedModelsCache;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Argon4W
 */
public class DefaultRendererBakedModelsCache implements RendererBakedModelsCache {
    private final Map<BakedModel, IBakedModelCache> modelCache;

    public DefaultRendererBakedModelsCache() {
        this.modelCache = new ConcurrentHashMap<>();
    }

    @Override
    public BakedModel getTransformedModel(BakedModel model, PoseStack poseStack) {
        return getTransformedModel(model, new Transformation(poseStack.last().pose()));
    }

    @Override
    public BakedModel getTransformedModel(BakedModel model, Transformation transformation) {
        return modelCache.compute(model, (model1, cache) -> cache == null ? createModelCache(model) : (cache.size() > 32 ? new DynamicModelCache(model1, this) : cache)).getTransformedModel(transformation);
    }

    @Override
    public int getSize() {
        return modelCache.values().stream().mapToInt(IBakedModelCache::size).sum();
    }

    public IBakedModelCache createModelCache(BakedModel model) {
        return model instanceof SimpleBakedModel simple ? new SimpleModelCache(simple) : new DynamicModelCache(model, this);
    }
}
