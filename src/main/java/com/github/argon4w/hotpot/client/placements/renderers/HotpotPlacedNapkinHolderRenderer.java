package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.github.argon4w.hotpot.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.placements.HotpotNapkinHolder;
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

public class HotpotNapkinHolderRenderer implements IHotpotPlacementRenderer {
    @Override
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, IHotpotPlacementContainerBlockEntity container, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (!(placement instanceof HotpotNapkinHolder napkinHolder)) {
            return;
        }

        float x = IHotpotPlacement.getSlotX(napkinHolder.getPos()) + 0.25f;
        float z = IHotpotPlacement.getSlotZ(napkinHolder.getPos()) + 0.25f;

        BakedModel napkinHolderModel = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "block/hotpot_napkin_holder")));
        BakedModel napkinModel = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "block/hotpot_napkin")));

        poseStack.pushPose();
        poseStack.translate(x, 0, z);
        poseStack.scale(0.68f, 0.68f, 0.68f);

        context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), null, napkinHolderModel, 0.8f, 0.3f, 0.3f, combinedLight, combinedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());

        poseStack.popPose();

        for (int i = 0; i < napkinHolder.getItemSlot().getStackCount(); i ++) {
            poseStack.pushPose();

            poseStack.translate(x, (0.0625f + 0.05f * i) * 0.68f, z);
            poseStack.mulPose(Axis.YP.rotationDegrees(napkinHolder.getDirection().toYRot() + (i % 2 == 0 ? 1 : -1) * 3));
            poseStack.scale(0.68f, 0.68f, 0.68f);

            context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), null, napkinModel, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());

            poseStack.popPose();
        }
    }
}
