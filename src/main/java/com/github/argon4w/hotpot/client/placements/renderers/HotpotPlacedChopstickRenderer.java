package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.client.sections.SectionGeometryRenderContext;
import com.github.argon4w.hotpot.placements.HotpotPlacedChopstick;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import com.github.argon4w.hotpot.placements.coords.ComplexDirection;
import com.github.argon4w.hotpot.placements.coords.HotpotPlacementPositions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.neoforged.neoforge.client.model.data.ModelData;

public class HotpotPlacedChopstickRenderer implements IHotpotPlacementRenderer {
    @Override
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, IHotpotPlacementContainer container, BlockPos pos, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float partialTick) {

    }

    @Override
    public void renderSectionGeometry(IHotpotPlacement placement, AddSectionGeometryEvent.SectionRenderingContext context, IHotpotPlacementContainer container, BlockPos pos, PoseStack poseStack, SectionGeometryRenderContext modelRenderContext) {
        if (!(placement instanceof HotpotPlacedChopstick placedChopstick)) {
            return;
        }

        int position1 = placedChopstick.getPosition1();
        int position2 = placedChopstick.getPosition2();
        ComplexDirection direction = ComplexDirection.between(position1, position2);

        double x1 = HotpotPlacementPositions.getRenderCenterX(position1);
        double z1 = HotpotPlacementPositions.getRenderCenterZ(position1);

        double x2 = HotpotPlacementPositions.getRenderCenterX(position2);
        double z2 = HotpotPlacementPositions.getRenderCenterZ(position2);

        double positionX = (x1 + x2) / 2;
        double positionY = (z1 + z2) / 2;

        poseStack.pushPose();
        poseStack.translate(positionX, 0.07f, positionY);
        poseStack.mulPose(Axis.YN.rotationDegrees((float) direction.toYRot()));
        poseStack.mulPose(Axis.XN.rotationDegrees(95));
        poseStack.scale(0.5f, 0.5f, 0.5f);

        modelRenderContext.renderUncachedItem(placedChopstick.getChopstickItemSlot().getItemStack(), ItemDisplayContext.NONE, false, poseStack, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(positionX, 0f, positionY);
        poseStack.mulPose(Axis.YN.rotationDegrees((float) direction.toYRot()));
        poseStack.scale(0.5f, 0.5f, 0.5f);

        BakedModel model = Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "block/hotpot_chopstick_stand")));
        modelRenderContext.renderCachedModel(model, poseStack, RenderType.solid(), OverlayTexture.NO_OVERLAY, ModelData.EMPTY);

        poseStack.popPose();
    }
}
