package com.github.argon4w.hotpot.client.blocks;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotElegantPlacementRackBlockEntity;
import com.github.argon4w.hotpot.client.placements.HotpotPlacementRenderers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;

public class HotpotElegantPlacementRackBlockEntityRenderer implements BlockEntityRenderer<HotpotElegantPlacementRackBlockEntity>, ISectionGeometryBLockEntityRenderer<HotpotElegantPlacementRackBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public HotpotElegantPlacementRackBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(HotpotElegantPlacementRackBlockEntity hotpotElegantPlacementRackBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        hotpotElegantPlacementRackBlockEntity.getPlacements0().forEach(placement -> placement.getPlacementSerializerHolder().unwrapKey().map(ResourceKey::location).ifPresent(key -> HotpotPlacementRenderers.getPlacementRenderer(key).render(placement, context, hotpotElegantPlacementRackBlockEntity, hotpotElegantPlacementRackBlockEntity.getBlockPos(), poseStack, bufferSource, combinedLight, combinedOverlay, partialTick)));

        poseStack.pushPose();

        poseStack.translate(0.05f, 0.1875f, 0.05f);
        poseStack.scale(0.9f, 0.9f, 0.9f);
        hotpotElegantPlacementRackBlockEntity.getPlacements1().forEach(placement -> placement.getPlacementSerializerHolder().unwrapKey().map(ResourceKey::location).ifPresent(key -> HotpotPlacementRenderers.getPlacementRenderer(key).render(placement, context, hotpotElegantPlacementRackBlockEntity, hotpotElegantPlacementRackBlockEntity.getBlockPos(), poseStack, bufferSource, combinedLight, combinedOverlay, partialTick)));

        poseStack.popPose();

        poseStack.pushPose();

        poseStack.translate(0.05f, 0.75f, 0.05f);
        poseStack.scale(0.9f, 0.9f, 0.9f);
        hotpotElegantPlacementRackBlockEntity.getPlacements2().forEach(placement -> placement.getPlacementSerializerHolder().unwrapKey().map(ResourceKey::location).ifPresent(key -> HotpotPlacementRenderers.getPlacementRenderer(key).render(placement, context, hotpotElegantPlacementRackBlockEntity, hotpotElegantPlacementRackBlockEntity.getBlockPos(), poseStack, bufferSource, combinedLight, combinedOverlay, partialTick)));

        poseStack.popPose();
    }

    @Override
    public void renderSectionGeometry(HotpotElegantPlacementRackBlockEntity hotpotElegantPlacementRackBlockEntity, AddSectionGeometryEvent.SectionRenderingContext context, PoseStack poseStack, BlockPos pos, ModelRenderer modelRenderer) {
        hotpotElegantPlacementRackBlockEntity.getPlacements0().forEach(placement -> placement.getPlacementSerializerHolder().unwrapKey().map(ResourceKey::location).ifPresent(key -> HotpotPlacementRenderers.getPlacementRenderer(key).renderSectionGeometry(placement, context, hotpotElegantPlacementRackBlockEntity, pos, poseStack, modelRenderer)));

        poseStack.pushPose();

        poseStack.translate(0.05f, 0.1875f, 0.05f);
        poseStack.scale(0.9f, 0.9f, 0.9f);
        hotpotElegantPlacementRackBlockEntity.getPlacements1().forEach(placement -> placement.getPlacementSerializerHolder().unwrapKey().map(ResourceKey::location).ifPresent(key -> HotpotPlacementRenderers.getPlacementRenderer(key).renderSectionGeometry(placement, context, hotpotElegantPlacementRackBlockEntity, pos, poseStack, modelRenderer)));

        poseStack.popPose();

        poseStack.pushPose();

        poseStack.translate(0.05f, 0.75f, 0.05f);
        poseStack.scale(0.9f, 0.9f, 0.9f);
        hotpotElegantPlacementRackBlockEntity.getPlacements2().forEach(placement -> placement.getPlacementSerializerHolder().unwrapKey().map(ResourceKey::location).ifPresent(key -> HotpotPlacementRenderers.getPlacementRenderer(key).renderSectionGeometry(placement, context, hotpotElegantPlacementRackBlockEntity, pos, poseStack, modelRenderer)));

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(HotpotElegantPlacementRackBlockEntity hotpotElegantPlacementRackBlockEntity) {
        return true;
    }

    @Override
    public boolean shouldRender(HotpotElegantPlacementRackBlockEntity p_173568_, Vec3 p_173569_) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}
