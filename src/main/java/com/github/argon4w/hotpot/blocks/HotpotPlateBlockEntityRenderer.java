package com.github.argon4w.hotpot.blocks;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class HotpotPlateBlockEntityRenderer extends TileEntityRenderer<HotpotPlaceableBlockEntity> {
    public HotpotPlateBlockEntityRenderer(TileEntityRendererDispatcher context) {
        super(context);
    }

    @Override
    public void render(HotpotPlaceableBlockEntity hotpotPlateBlockEntity, float partialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay) {
        hotpotPlateBlockEntity.getPlaceables().forEach(plate -> plate.render(renderer, hotpotPlateBlockEntity, partialTick, poseStack, bufferSource, combinedLight, combinedOverlay));
    }

    @Override
    public boolean shouldRenderOffScreen(HotpotPlaceableBlockEntity hotpotBlockEntity) {
        return false;
    }
}
