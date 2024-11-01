package com.github.argon4w.hotpot.client.blocks;

import com.github.argon4w.hotpot.api.client.sections.IBlockEntitySectionGeometryRenderer;
import com.github.argon4w.hotpot.api.client.sections.ISectionGeometryRenderContext;
import com.github.argon4w.hotpot.blocks.HotpotTestBenchBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;

public class HotpotTestBenchBlockEntityRenderer
        implements BlockEntityRenderer<HotpotTestBenchBlockEntity>,
                IBlockEntitySectionGeometryRenderer<HotpotTestBenchBlockEntity> {
    @Override
    public void render(
            HotpotTestBenchBlockEntity pBlockEntity,
            float pPartialTick,
            PoseStack stack,
            MultiBufferSource pBufferSource,
            int pPackedLight,
            int pPackedOverlay) {}

    @Override
    public void renderSectionGeometry(
            HotpotTestBenchBlockEntity blockEntity,
            AddSectionGeometryEvent.SectionRenderingContext context,
            PoseStack stack,
            BlockPos blockPos,
            BlockPos regionOrigin,
            ISectionGeometryRenderContext modelRenderContext) {}
}
