package com.github.argon4w.hotpot.client.sections.cache;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.BakedModel;
import net.neoforged.neoforge.client.model.IQuadTransformer;

public interface IHotpotModelCache {
    BakedModel getTransformedModel(Transformation transformation);
    int size();
}
