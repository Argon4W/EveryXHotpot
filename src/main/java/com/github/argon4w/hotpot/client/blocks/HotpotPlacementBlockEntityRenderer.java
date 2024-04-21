package com.github.argon4w.hotpot.client.blocks;

import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.client.placements.HotpotPlacementRenderers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;

public class HotpotPlacementBlockEntityRenderer implements BlockEntityRenderer<HotpotPlacementBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public HotpotPlacementBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(HotpotPlacementBlockEntity hotpotPlateBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        hotpotPlateBlockEntity.getPlacements().forEach(placement -> HotpotPlacementRenderers.getPlacementRenderer(placement.getResourceLocation()).render(placement, context, hotpotPlateBlockEntity, partialTick, poseStack, bufferSource, combinedLight, combinedOverlay));
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
