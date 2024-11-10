package com.github.argon4w.hotpot.client.blocks;

import com.github.argon4w.hotpot.api.client.sections.IBlockEntitySectionGeometryRenderer;
import com.github.argon4w.hotpot.api.client.sections.ISectionGeometryRenderContext;
import com.github.argon4w.hotpot.blocks.HotpotPlacementRackBlockEntity;
import com.github.argon4w.hotpot.client.placements.HotpotPlacementRenderers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import org.jetbrains.annotations.NotNull;

public class HotpotPlacementRackBlockEntityRenderer implements
        BlockEntityRenderer<HotpotPlacementRackBlockEntity>,
        IBlockEntitySectionGeometryRenderer<HotpotPlacementRackBlockEntity> {

    @Override
    public void render(
            HotpotPlacementRackBlockEntity hotpotPlacementRackBlockEntity,
            float partialTick,
            PoseStack poseStack,
            @NotNull MultiBufferSource bufferSource,
            int combinedLight,
            int combinedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.05f, 0.15625f, 0.05f);
        poseStack.scale(0.9f, 0.9f, 0.9f);

        hotpotPlacementRackBlockEntity.getPlacements1().forEach(placement -> placement
                .getPlacementSerializerHolder()
                .unwrapKey()
                .map(ResourceKey::location)
                .ifPresent(key -> HotpotPlacementRenderers.getPlacementRenderer(key).render(
                        placement,
                        hotpotPlacementRackBlockEntity,
                        hotpotPlacementRackBlockEntity.getBlockPos(),
                        poseStack,
                        bufferSource,
                        combinedLight,
                        combinedOverlay,
                        partialTick)));

        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.05f, 0.71875f, 0.05f);
        poseStack.scale(0.9f, 0.9f, 0.9f);

        hotpotPlacementRackBlockEntity.getPlacements2().forEach(placement -> placement
                .getPlacementSerializerHolder()
                .unwrapKey()
                .map(ResourceKey::location)
                .ifPresent(key -> HotpotPlacementRenderers.getPlacementRenderer(key).render(
                        placement,
                        hotpotPlacementRackBlockEntity,
                        hotpotPlacementRackBlockEntity.getBlockPos(),
                        poseStack,
                        bufferSource,
                        combinedLight,
                        combinedOverlay,
                        partialTick)));

        poseStack.popPose();
    }

    @Override
    public void renderSectionGeometry(
            HotpotPlacementRackBlockEntity hotpotPlacementRackBlockEntity,
            AddSectionGeometryEvent.SectionRenderingContext context,
            PoseStack poseStack,
            BlockPos pos,
            BlockPos regionOrigin,
            ISectionGeometryRenderContext modelRenderContext) {
        poseStack.pushPose();

        poseStack.translate(0.05f, 0.15625f, 0.05f);
        poseStack.scale(0.9f, 0.9f, 0.9f);
        hotpotPlacementRackBlockEntity.getPlacements1().forEach(placement -> placement
                .getPlacementSerializerHolder()
                .unwrapKey()
                .map(ResourceKey::location)
                .ifPresent(key -> HotpotPlacementRenderers.getPlacementRenderer(key)
                        .renderSectionGeometry(
                                placement,
                                context,
                                hotpotPlacementRackBlockEntity,
                                pos,
                                poseStack,
                                modelRenderContext)));

        poseStack.popPose();

        poseStack.pushPose();

        poseStack.translate(0.05f, 0.71875f, 0.05f);
        poseStack.scale(0.9f, 0.9f, 0.9f);
        hotpotPlacementRackBlockEntity.getPlacements2().forEach(placement -> placement
                .getPlacementSerializerHolder()
                .unwrapKey()
                .map(ResourceKey::location)
                .ifPresent(key -> HotpotPlacementRenderers.getPlacementRenderer(key)
                        .renderSectionGeometry(
                                placement,
                                context,
                                hotpotPlacementRackBlockEntity,
                                pos,
                                poseStack,
                                modelRenderContext)));

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(HotpotPlacementRackBlockEntity hotpotPlacementRackBlockEntity) {
        return true;
    }

    @Override
    public boolean shouldRender(HotpotPlacementRackBlockEntity p_173568_, Vec3 p_173569_) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}
