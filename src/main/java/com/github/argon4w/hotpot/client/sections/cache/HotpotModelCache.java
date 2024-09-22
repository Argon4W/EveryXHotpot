package com.github.argon4w.hotpot.client.sections.cache;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HotpotModelCache {
    private final Map<BakedModel, IHotpotModelCache> modelCache;

    public HotpotModelCache() {
        this.modelCache = new ConcurrentHashMap<>();
    }

    public BakedModel getTransformedModel(BakedModel model, PoseStack poseStack) {
        return getTransformedModel(model, new Transformation(poseStack.last().pose()));
    }

    public BakedModel getTransformedModel(BakedModel model, Transformation transformation) {
        return modelCache.compute(model, (model1, cache) -> cache == null ? createModelCache(model) : ((cache instanceof HotpotSimpleModelCache simple && simple.size() > 32) ? new HotpotDynamicModelCache(model1, this) : cache)).getTransformedModel(transformation);
    }

    public IHotpotModelCache createModelCache(BakedModel model) {
        return model instanceof SimpleBakedModel simple ? new HotpotSimpleModelCache(simple) : new HotpotDynamicModelCache(model, this);
    }
}
