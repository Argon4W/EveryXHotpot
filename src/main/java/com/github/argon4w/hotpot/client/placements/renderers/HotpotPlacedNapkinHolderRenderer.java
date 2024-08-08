package com.github.argon4w.hotpot.client.placements.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainerBlockEntity;
import com.github.argon4w.hotpot.client.placements.IHotpotPlacementRenderer;
import com.github.argon4w.hotpot.placements.HotpotPlacedNapkinHolder;
import com.github.argon4w.hotpot.placements.HotpotPlacementSerializers;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Math;

public class HotpotPlacedNapkinHolderRenderer implements IHotpotPlacementRenderer {
    @Override
    public void render(IHotpotPlacement placement, BlockEntityRendererProvider.Context context, IHotpotPlacementContainerBlockEntity container, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, LevelBlockPos pos) {
        if (!(placement instanceof HotpotPlacedNapkinHolder napkinHolder)) {
            return;
        }

        float x = HotpotPlacementSerializers.getSlotX(napkinHolder.getPos()) + 0.25f;
        float z = HotpotPlacementSerializers.getSlotZ(napkinHolder.getPos()) + 0.25f;

        BakedModel napkinHolderModel = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "block/hotpot_napkin_holder")));
        BakedModel napkinModel = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "block/hotpot_napkin")));

        int color = DyedItemColor.getOrDefault(napkinHolder.getNapkinHolderItemSlot().getItemStack(), -1);
        float r = FastColor.ARGB32.red(color) / 255.0f;
        float g = FastColor.ARGB32.green(color) / 255.0f;
        float b = FastColor.ARGB32.blue(color) / 255.0f;

        RandomSource randomSource = RandomSource.create();
        randomSource.setSeed(color * napkinHolder.getPos() * (pos.pos() != null ? pos.pos().hashCode() : 1L) + 42L);
        float randomDegrees = Math.clamp((float) randomSource.nextGaussian(), 0.0f, 1.0f) * 15.0f - 7.5f;

        poseStack.pushPose();
        poseStack.translate(x, 0, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(360.0f - napkinHolder.getDirection().toYRot() - randomDegrees));
        poseStack.scale(0.68f, 0.68f, 0.68f);

        context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), null, napkinHolderModel, r, g, b, combinedLight, combinedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());

        poseStack.popPose();

        for (int i = 0; i < napkinHolder.getNapkinItemSlot().getStackCount(); i ++) {
            poseStack.pushPose();

            poseStack.translate(x, (0.0625f + 0.05f * i) * 0.68f, z);
            poseStack.mulPose(Axis.YP.rotationDegrees((360.0f - napkinHolder.getDirection().toYRot()) + (i % 2 == 0 ? 1 : -1) * 3 - randomDegrees));
            poseStack.scale(0.68f, 0.68f, 0.68f);

            context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), null, napkinModel, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());

            poseStack.popPose();
        }
    }
}
