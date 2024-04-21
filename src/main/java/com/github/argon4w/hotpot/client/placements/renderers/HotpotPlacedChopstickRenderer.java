package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.placements.HotpotPlacedChopstick;
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

public class HotpotPlacedChopstickRenderer implements IHotpotPlacementRenderer {
    @Override
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, HotpotPlacementBlockEntity hotpotBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (!(placement instanceof HotpotPlacedChopstick placedChopstick)) {
            return;
        }

        float x1 = IHotpotPlacement.getSlotX(placedChopstick.getPos1()) + 0.25f;
        float z1 = IHotpotPlacement.getSlotZ(placedChopstick.getPos1()) + 0.25f;

        float x2 = IHotpotPlacement.getSlotX(placedChopstick.getPos2()) + 0.25f;
        float z2 = IHotpotPlacement.getSlotZ(placedChopstick.getPos2()) + 0.25f;

        poseStack.pushPose();
        poseStack.translate((x1 + x2) / 2, 0.07f, (z1 + z2) / 2);
        poseStack.mulPose(Axis.YN.rotationDegrees(placedChopstick.getDirection().toYRot()));
        poseStack.mulPose(Axis.XN.rotationDegrees(95));
        poseStack.scale(0.5f, 0.5f, 0.5f);

        context.getItemRenderer().renderStatic(null, placedChopstick.getChopstickItemStack(), ItemDisplayContext.NONE, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemDisplayContext.NONE.ordinal());

        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate((x1 + x2) / 2, 0f, (z1 + z2) / 2);
        poseStack.mulPose(Axis.YN.rotationDegrees(placedChopstick.getDirection().toYRot()));
        poseStack.scale(0.5f, 0.5f, 0.5f);

        BakedModel model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(new ResourceLocation(HotpotModEntry.MODID, "block/hotpot_chopstick_stand"));
        context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), null, model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());

        poseStack.popPose();
    }
}
