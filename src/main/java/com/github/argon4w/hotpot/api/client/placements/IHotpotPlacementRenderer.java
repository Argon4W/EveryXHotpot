package com.github.argon4w.hotpot.api.client.placements;

import com.github.argon4w.hotpot.api.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.client.sections.ISectionGeometryRenderContext;
import com.github.argon4w.hotpot.api.placements.IHotpotPlacement;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;

public interface IHotpotPlacementRenderer {
    void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, IHotpotPlacementContainer container, BlockPos pos, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float partialTick);
    void renderSectionGeometry(IHotpotPlacement placement, AddSectionGeometryEvent.SectionRenderingContext context, IHotpotPlacementContainer container, BlockPos pos, PoseStack poseStack, ISectionGeometryRenderContext modelRenderContext);
}
