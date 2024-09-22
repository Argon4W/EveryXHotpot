package com.github.argon4w.hotpot.client.sections.cache;

import com.github.argon4w.hotpot.EntryStreams;
import com.github.argon4w.hotpot.client.sections.ISimpleBakedModelExtension;
import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HotpotSimpleModelCache implements IHotpotModelCache {
    private final SimpleBakedModel model;
    private final Map<Transformation, BakedModel> modelCache;

    public HotpotSimpleModelCache(SimpleBakedModel model) {
        this.model = model;
        this.modelCache = new ConcurrentHashMap<>();
    }

    @Override
    public BakedModel getTransformedModel(Transformation transformation) {
        return modelCache.computeIfAbsent(transformation, transformation1 -> getTransformedModel(QuadTransformers.applying(transformation1)));
    }

    public BakedModel getTransformedModel(IQuadTransformer transformer) {
        return new SimpleBakedModel(model.unculledFaces.stream().map(transformer::process).toList(), model.culledFaces.entrySet().stream().map(EntryStreams.mapEntryValue(list -> list.stream().map(transformer::process).toList())).collect(EntryStreams.of()), model.useAmbientOcclusion(), model.usesBlockLight(), model.isGui3d(), model.getParticleIcon(), model.getTransforms(), model.getOverrides(), model instanceof ISimpleBakedModelExtension extension ? extension.everyxhotpot$getRenderTypeGroup() : RenderTypeGroup.EMPTY);
    }

    @Override
    public int size() {
        return modelCache.size();
    }
}
