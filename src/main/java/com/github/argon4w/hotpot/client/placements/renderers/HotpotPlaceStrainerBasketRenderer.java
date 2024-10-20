package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.api.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.api.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.api.placements.IHotpotPlacement;
import com.github.argon4w.hotpot.client.sections.ISectionGeometryRenderContext;
import com.github.argon4w.hotpot.placements.HotpotPlacedStrainerBasket;
import com.github.argon4w.hotpot.placements.coords.HotpotPlacementPositions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;

public class HotpotPlaceStrainerBasketRenderer implements IHotpotPlacementRenderer {
    @Override
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, IHotpotPlacementContainer container, BlockPos pos, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float partialTick) {

    }

    @Override
    public void renderSectionGeometry(IHotpotPlacement placement, AddSectionGeometryEvent.SectionRenderingContext context, IHotpotPlacementContainer container, BlockPos pos, PoseStack poseStack, ISectionGeometryRenderContext modelRenderContext) {
        if (!(placement instanceof HotpotPlacedStrainerBasket placedStrainerBasket)) {
            return;
        }

        double x = HotpotPlacementPositions.getRenderCenterX(placedStrainerBasket.getPosition());
        double z = HotpotPlacementPositions.getRenderCenterZ(placedStrainerBasket.getPosition());
        double scale = 0.6;
        double rotationY = placedStrainerBasket.getDirection().toYRot();
        double positionY = 0.5 * scale + 0.01;

        ItemStack strainerBasketItemStack = placedStrainerBasket.getStrainerBasketItemSlot().getItemStack();

        poseStack.pushPose();
        poseStack.translate(x, positionY, z);
        poseStack.mulPose(Axis.YN.rotationDegrees((float) rotationY));
        poseStack.scale((float) scale, (float) scale, (float) scale);

        modelRenderContext.renderUncachedItem(strainerBasketItemStack, ItemDisplayContext.NONE, false, poseStack, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }
}
