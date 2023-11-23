package com.github.argon4w.hotpot.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class HotpotPlateBlockEntityRenderer implements BlockEntityRenderer<HotpotPlaceableBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public HotpotPlateBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(HotpotPlaceableBlockEntity hotpotPlateBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        hotpotPlateBlockEntity.getPlaceables().forEach(plate -> plate.render(context, hotpotPlateBlockEntity, partialTick, poseStack, bufferSource, combinedLight, combinedOverlay));
    }

    @Override
    public boolean shouldRenderOffScreen(HotpotPlaceableBlockEntity hotpotBlockEntity) {
        return false;
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}
