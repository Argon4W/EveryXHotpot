package com.github.argon4w.hotpot.client.sections.cache;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.BakedModel;

public class UncachedRendererBakedModelsCache implements RendererBakedModelsCache {
    @Override
    public BakedModel getTransformedModel(BakedModel model, PoseStack poseStack) {
        return getTransformedModel(model, new Transformation(poseStack.last().pose()));
    }

    @Override
    public BakedModel getTransformedModel(BakedModel model, Transformation transformation) {
        return new DynamicTransformedBakedModel(model, transformation, this);
    }

    @Override
    public int getSize() {
        return 0;
    }
}
