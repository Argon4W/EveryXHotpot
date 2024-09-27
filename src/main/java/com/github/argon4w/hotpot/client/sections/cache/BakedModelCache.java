package com.github.argon4w.hotpot.client.sections.cache;

import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.BakedModel;

/**
 * @author Argon4W
 */
public interface BakedModelCache {
    BakedModel getTransformedModel(Transformation transformation);
    int size();
}
