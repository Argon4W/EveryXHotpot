package com.github.argon4w.hotpot.client.sections.cache;

import com.github.argon4w.hotpot.client.sections.TransformedBakedModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record HotpotDynamicModelCache(BakedModel model, HotpotModelCache cache) implements IHotpotModelCache {
    @Override
    public BakedModel getTransformedModel(Transformation transformation) {
        return new TransformedBakedModel(model, transformation, cache);
    }

    @Override
    public int size() {
        return 0;
    }
}
