package com.github.argon4w.hotpot.client.blocks;

import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.client.placements.HotpotPlacementRenderers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;

public class HotpotPlacementBlockEntityRenderer implements BlockEntityRenderer<HotpotPlacementBlockEntity>, IHotpotSectionGeometryBLockEntityRenderer<HotpotPlacementBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public HotpotPlacementBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(HotpotPlacementBlockEntity hotpotPlacementBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        hotpotPlacementBlockEntity.getPlacements().forEach(placement -> placement.getPlacementSerializerHolder().unwrapKey().map(ResourceKey::location).ifPresent(key -> HotpotPlacementRenderers.getPlacementRenderer(key).render(placement, context, hotpotPlacementBlockEntity, hotpotPlacementBlockEntity.getBlockPos(), poseStack, bufferSource, combinedLight, combinedOverlay, partialTick)));
    }

    @Override
    public void renderSectionGeometry(HotpotPlacementBlockEntity hotpotPlacementBlockEntity, AddSectionGeometryEvent.SectionRenderingContext context, PoseStack poseStack, BlockPos pos, ModelRenderer modelRenderer) {
        hotpotPlacementBlockEntity.getPlacements().forEach(placement -> placement.getPlacementSerializerHolder().unwrapKey().map(ResourceKey::location).ifPresent(key -> HotpotPlacementRenderers.getPlacementRenderer(key).renderSectionGeometry(placement, context, hotpotPlacementBlockEntity, pos, poseStack, modelRenderer)));
    }

    @Override
    public boolean shouldRenderOffScreen(HotpotPlacementBlockEntity hotpotBlockEntity) {
        return true;
    }

    @Override
    public boolean shouldRender(HotpotPlacementBlockEntity p_173568_, Vec3 p_173569_) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}
