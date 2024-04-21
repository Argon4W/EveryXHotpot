package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.placements.HotpotSmallPlate;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.data.ModelData;

public class HotpotSmallPlateRenderer implements IHotpotPlacementRenderer {
    @Override
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, HotpotPlacementBlockEntity hotpotBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (!(placement instanceof HotpotSmallPlate smallPlate)) {
            return;
        }

        float x = IHotpotPlacement.getSlotX(smallPlate.getPos1()) + 0.25f;
        float z = IHotpotPlacement.getSlotZ(smallPlate.getPos1()) + 0.25f;

        poseStack.pushPose();
        poseStack.translate(x, 0f, z);
        poseStack.scale(0.68f, 0.68f, 0.68f);

        BakedModel model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(new ResourceLocation(HotpotModEntry.MODID, "block/hotpot_plate_small"));
        context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), null, model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());

        poseStack.popPose();

        for (int i = 0; i < smallPlate.getItemSlot().getStackCount(); i ++) {
            poseStack.pushPose();

            poseStack.translate(x, 0.05f + 0.02 * i, z);
            poseStack.mulPose(Axis.YP.rotationDegrees(smallPlate.getDirection().toYRot() + (i % 2) * 20));
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            poseStack.scale(0.35f, 0.35f, 0.35f);

            context.getItemRenderer().renderStatic(smallPlate.getItemSlot().getItemStack(), ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, null, ItemDisplayContext.GROUND.ordinal());

            poseStack.popPose();
        }
    }
}
