package com.github.argon4w.hotpot.client.sections.cache;

import com.github.argon4w.fancytoys.streams.EntryStream;
import com.github.argon4w.hotpot.api.client.sections.cache.IBakedModelCache;
import com.github.argon4w.hotpot.client.sections.ISimpleBakedModelExtension;
import com.mojang.math.Transformation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;

/**
 * @author Argon4W
 */
public class SimpleModelCache implements IBakedModelCache {

    private final SimpleBakedModel model;
    private final Map<Transformation, BakedModel> modelCache;

    public SimpleModelCache(SimpleBakedModel model) {
        this.model = model;
        this.modelCache = new ConcurrentHashMap<>();
    }

    public BakedModel getTransformedModel(IQuadTransformer transformer) {
        return new SimpleBakedModel(
                model.unculledFaces
                        .stream()
                        .map(transformer::process)
                        .toList(),
                EntryStream
                        .fromMap(model.culledFaces)
                        .mapValue(list -> list.stream().map(transformer::process).toList())
                        .toMap(),
                model.useAmbientOcclusion(),
                model.usesBlockLight(),
                model.isGui3d(),
                model.getParticleIcon(),
                model.getTransforms(),
                model.getOverrides(),
                model instanceof ISimpleBakedModelExtension extension
                        ? extension.everyxhotpot$getRenderTypeGroup()
                        : RenderTypeGroup.EMPTY);
    }

    @Override
    public BakedModel getTransformedModel(Transformation transformation) {
        return modelCache.computeIfAbsent(
                transformation,
                transformation1 -> getTransformedModel(QuadTransformers.applying(transformation1)));
    }

    @Override
    public int size() {
        return modelCache.size();
    }
}
