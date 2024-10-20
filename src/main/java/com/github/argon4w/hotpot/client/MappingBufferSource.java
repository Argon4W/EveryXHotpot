package com.github.argon4w.hotpot.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record MappingBufferSource(MultiBufferSource bufferSource, Map<RenderType, RenderType> map) implements MultiBufferSource {
    public static final Map<RenderType, RenderType> ITEM_BUFFER_SOURCE_MAP = Map.of(Sheets.translucentCullBlockSheet(), Sheets.cutoutBlockSheet());

    @NotNull
    @Override
    public VertexConsumer getBuffer(@NotNull RenderType renderType) {
        return bufferSource.getBuffer(map.getOrDefault(renderType, renderType));
    }

    public static MultiBufferSource itemBufferSource(MultiBufferSource bufferSource) {
        return new MappingBufferSource(bufferSource, ITEM_BUFFER_SOURCE_MAP);
    }
}
