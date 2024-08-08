package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.SimpleItemSlot;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.github.argon4w.hotpot.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.placements.HotpotLongPlate;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.data.ModelData;

public class HotpotLongPlateRenderer implements IHotpotPlacementRenderer {

    @Override
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, IHotpotPlacementContainerBlockEntity container, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, LevelBlockPos pos) {
        if (!(placement instanceof HotpotLongPlate longPlate)) {
            return;
        }

        float x1 = IHotpotPlacement.getSlotX(longPlate.getPos1()) + 0.25f;
        float z1 = IHotpotPlacement.getSlotZ(longPlate.getPos1()) + 0.25f;

        float x2 = IHotpotPlacement.getSlotX(longPlate.getPos2()) + 0.25f;
        float z2 = IHotpotPlacement.getSlotZ(longPlate.getPos2()) + 0.25f;

        int plateCount = 0;

        for (; plateCount < longPlate.getPlateItemSlot().getStackCount(8); plateCount ++) {
            poseStack.pushPose();
            poseStack.translate((x1 + x2) / 2, plateCount * 0.0625f, (z1 + z2) / 2);
            poseStack.mulPose(Axis.YP.rotationDegrees(longPlate.getDirection().toYRot()));
            poseStack.scale(0.68f, 0.68f, 0.68f);

            BakedModel model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "block/hotpot_plate_long")));
            context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), null, model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());

            poseStack.popPose();
        }

        int i = 0;

        for (int k = 0; k < longPlate.getItemSlot1().getStackCount(); k ++, i ++) {
            renderLargePlateItem(longPlate, context, poseStack, bufferSource, combinedLight, combinedOverlay, longPlate.getItemSlot1(), x1, z1, i, plateCount);
        }

        for (int k = 0; k < longPlate.getItemSlot2().getStackCount(); k ++, i ++) {
            renderLargePlateItem(longPlate, context, poseStack, bufferSource, combinedLight, combinedOverlay, longPlate.getItemSlot2(), x1, z1, i, plateCount);
        }
    }

    public void renderLargePlateItem(HotpotLongPlate longPlate, BlockEntityRendererProvider.Context context, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, SimpleItemSlot slot, float x, float z, int index, int plateCount) {
        poseStack.pushPose();
        poseStack.translate(x, 0.0175f + 0.0625f * plateCount, z);
        poseStack.mulPose(Axis.YN.rotationDegrees(longPlate.getDirection().toYRot()));
        poseStack.translate(0f, 0f, -0.07f + index * 0.09f);
        poseStack.mulPose(Axis.XN.rotationDegrees(75));
        poseStack.scale(0.35f, 0.35f, 0.35f);

        context.getItemRenderer().renderStatic(slot.getItemStack(), ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, null, ItemDisplayContext.GROUND.ordinal());

        poseStack.popPose();
    }
}
