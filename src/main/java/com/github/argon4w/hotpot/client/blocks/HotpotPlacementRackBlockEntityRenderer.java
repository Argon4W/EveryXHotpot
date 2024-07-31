package com.github.argon4w.hotpot.client.blocks;

import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.blocks.HotpotPlacementRackBlockEntity;
import com.github.argon4w.hotpot.client.placements.HotpotPlacementRenderers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.phys.Vec3;

public class HotpotPlacementRackBlockEntityRenderer implements BlockEntityRenderer<HotpotPlacementRackBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public HotpotPlacementRackBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(HotpotPlacementRackBlockEntity hotpotPlacementRackBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        poseStack.pushPose();

        poseStack.translate(0.05f, 0.15625f, 0.05f);
        poseStack.scale(0.9f, 0.9f, 0.9f);
        hotpotPlacementRackBlockEntity.getPlacements1().forEach(placement -> placement.getPlacementFactoryHolder().unwrapKey().map(ResourceKey::location).ifPresent(key -> HotpotPlacementRenderers.getPlacementRenderer(key).render(placement, context, hotpotPlacementRackBlockEntity, partialTick, poseStack, bufferSource, combinedLight, combinedOverlay)));

        poseStack.popPose();

        poseStack.pushPose();

        poseStack.translate(0.05f, 0.71875f, 0.05f);
        poseStack.scale(0.9f, 0.9f, 0.9f);
        hotpotPlacementRackBlockEntity.getPlacements2().forEach(placement -> placement.getPlacementFactoryHolder().unwrapKey().map(ResourceKey::location).ifPresent(key -> HotpotPlacementRenderers.getPlacementRenderer(key).render(placement, context, hotpotPlacementRackBlockEntity, partialTick, poseStack, bufferSource, combinedLight, combinedOverlay)));

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
