package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.github.argon4w.hotpot.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.placements.HotpotPlacementSerializers;
import com.github.argon4w.hotpot.placements.HotpotSmallPlate;
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

public class HotpotSmallPlateRenderer implements IHotpotPlacementRenderer {
    @Override
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, IHotpotPlacementContainerBlockEntity container, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, LevelBlockPos pos) {
        if (!(placement instanceof HotpotSmallPlate smallPlate)) {
            return;
        }

        float x = HotpotPlacementSerializers.getSlotX(smallPlate.getPos()) + 0.25f;
        float z = HotpotPlacementSerializers.getSlotZ(smallPlate.getPos()) + 0.25f;

        int plateCount = 0;

        for (; plateCount < smallPlate.getPlateItemSlot().getRenderCount(8); plateCount++) {
            float positionY = plateCount * 0.0625f;

            poseStack.pushPose();
            poseStack.translate(x, positionY, z);
            poseStack.scale(0.68f, 0.68f, 0.68f);

            BakedModel model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "block/hotpot_plate_small")));
            context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), null, model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());

            poseStack.popPose();
        }

        for (int i = 0; i < smallPlate.getItemSlot().getRenderCount(); i ++) {
            float positionY = plateCount * 0.0625f + 0.02f * i;
            float rotationY = smallPlate.getDirection().toYRot() + (i % 2) * 20;

            poseStack.pushPose();

            poseStack.translate(x, positionY, z);
            poseStack.mulPose(Axis.YP.rotationDegrees(rotationY));
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            poseStack.scale(0.35f, 0.35f, 0.35f);

            context.getItemRenderer().renderStatic(smallPlate.getItemSlot().getItemStack(), ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, null, ItemDisplayContext.GROUND.ordinal());

            poseStack.popPose();
        }
    }
}
