package com.github.argon4w.hotpot.client.blocks;

import com.github.argon4w.hotpot.blocks.HotpotTestBenchBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.neoforged.neoforge.client.model.data.ModelData;

public class HotpotTestBenchBlockEntityRenderer implements BlockEntityRenderer<HotpotTestBenchBlockEntity>, ISectionGeometryBLockEntityRenderer {
    public HotpotTestBenchBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(HotpotTestBenchBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

    }

    @Override
    public void renderSectionGeometry(AddSectionGeometryEvent.SectionRenderingContext context, PoseStack stack, BlockPos blockPos, ModelRenderer modelRenderer) {
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(Blocks.DIRT.defaultBlockState());

        stack.pushPose();

        stack.translate(0.5, 0.5, 0.5);
        stack.mulPose(blockPos.getY() % 2 == 0 ? Axis.YP.rotationDegrees(45) : Axis.YP.rotationDegrees(200));
        stack.translate(-0.5, -0.5, -0.5);

        modelRenderer.renderModel(model, stack, RenderType.solid(), OverlayTexture.NO_OVERLAY, ModelData.EMPTY);

        stack.popPose();
    }
}
