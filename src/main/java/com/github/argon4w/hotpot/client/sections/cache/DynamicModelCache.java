package com.github.argon4w.hotpot.client.sections.cache;

import com.github.argon4w.hotpot.api.client.sections.cache.IBakedModelCache;
import com.github.argon4w.hotpot.api.client.sections.cache.RendererBakedModelsCache;
import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.BakedModel;

/**
 * @author Argon4W
 */
public record DynamicModelCache(BakedModel model, RendererBakedModelsCache cache) implements IBakedModelCache {
    @Override
    public BakedModel getTransformedModel(Transformation transformation) {
        return new DynamicTransformedBakedModel(model, transformation, cache);
    }

    @Override
    public int size() {
        return 0;
    }
}
