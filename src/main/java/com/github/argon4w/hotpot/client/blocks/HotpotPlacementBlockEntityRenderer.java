package com.github.argon4w.hotpot.client.blocks;

import com.github.argon4w.hotpot.api.client.sections.IBlockEntitySectionGeometryRenderer;
import com.github.argon4w.hotpot.api.client.sections.ISectionGeometryRenderContext;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.client.placements.HotpotPlacementRenderers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import org.jetbrains.annotations.NotNull;

public class HotpotPlacementBlockEntityRenderer implements
        BlockEntityRenderer<HotpotPlacementBlockEntity>,
        IBlockEntitySectionGeometryRenderer<HotpotPlacementBlockEntity> {

    @Override
    public void render(
            HotpotPlacementBlockEntity hotpotPlacementBlockEntity,
            float partialTick,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource bufferSource,
            int combinedLight,
            int combinedOverlay) {
        hotpotPlacementBlockEntity.getPlacements(0).forEach(placement -> placement
                .getPlacementSerializerHolder()
                .unwrapKey()
                .map(ResourceKey::location)
                .ifPresent(key -> HotpotPlacementRenderers.getPlacementRenderer(key).render(
                        placement,
                        hotpotPlacementBlockEntity,
                        hotpotPlacementBlockEntity.getBlockPos(),
                        poseStack,
                        bufferSource,
                        combinedLight,
                        combinedOverlay,
                        partialTick)));
    }

    @Override
    public void renderSectionGeometry(
            HotpotPlacementBlockEntity hotpotPlacementBlockEntity,
            AddSectionGeometryEvent.SectionRenderingContext context,
            PoseStack poseStack,
            BlockPos pos,
            BlockPos regionOrigin,
            ISectionGeometryRenderContext modelRenderContext) {
        hotpotPlacementBlockEntity.getPlacements(0).forEach(placement -> placement
                .getPlacementSerializerHolder()
                .unwrapKey()
                .map(ResourceKey::location)
                .ifPresent(key -> HotpotPlacementRenderers.getPlacementRenderer(key).renderSectionGeometry(
                        placement,
                        context,
                        hotpotPlacementBlockEntity,
                        pos,
                        poseStack,
                        modelRenderContext)));
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull HotpotPlacementBlockEntity hotpotBlockEntity) {
        return true;
    }

    @Override
    public boolean shouldRender(@NotNull HotpotPlacementBlockEntity p_173568_, @NotNull Vec3 p_173569_) {
        return true;
    }
}
