package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.github.argon4w.hotpot.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.placements.HotpotLargeRoundPlate;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import com.github.argon4w.hotpot.placements.SimpleItemSlot;
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

public class HotpotLargeRoundPlateRenderer implements IHotpotPlacementRenderer {
    @Override
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, IHotpotPlacementContainerBlockEntity container, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, LevelBlockPos pos) {
        if (!(placement instanceof HotpotLargeRoundPlate largeRoundPlate)) {
            return;
        }

        int plateCount = 0;

        for (; plateCount < largeRoundPlate.getPlateItemSlot().getStackCount(8); plateCount++) {
            poseStack.pushPose();

            poseStack.translate(0.5f, plateCount * 0.0625f, 0.5f);
            poseStack.scale(0.66f, 0.66f, 0.66f);

            BakedModel model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "block/hotpot_plate_large_round")));
            context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), null, model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());

            poseStack.popPose();
        }

        int[] mapped = {0, 1, 3, 2};

        for (int i = 0; i < 4; i ++) {
            SimpleItemSlot slot = largeRoundPlate.getSlots()[mapped[i]];
            float startDegree = 360.0f / 4.0f * i;

            for (int j = 0; j < slot.getStackCount(); j ++) {
                float stepDegree = 360.0f / 16.0f * j;

                poseStack.pushPose();

                poseStack.translate(0.5f, 0.0175f + 0.0625f * plateCount, 0.5f);
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0f + startDegree + stepDegree));
                poseStack.translate(0.25f, 0.0f, 0.0f);
                poseStack.mulPose(Axis.XP.rotationDegrees(75));

                poseStack.scale(0.35f, 0.35f, 0.35f);

                context.getItemRenderer().renderStatic(slot.getItemStack(), ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, null, ItemDisplayContext.FIXED.ordinal());

                poseStack.popPose();
            }
        }
    }
}
