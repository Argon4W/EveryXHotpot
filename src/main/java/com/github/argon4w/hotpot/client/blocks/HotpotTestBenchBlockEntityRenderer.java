package com.github.argon4w.hotpot.client.blocks;

import com.github.argon4w.hotpot.blocks.HotpotTestBenchBlockEntity;
import com.github.argon4w.hotpot.api.client.sections.IBlockEntitySectionGeometryRenderer;
import com.github.argon4w.hotpot.client.sections.ISectionGeometryRenderContext;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;

public class HotpotTestBenchBlockEntityRenderer implements BlockEntityRenderer<HotpotTestBenchBlockEntity>, IBlockEntitySectionGeometryRenderer<HotpotTestBenchBlockEntity> {
    public HotpotTestBenchBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(HotpotTestBenchBlockEntity pBlockEntity, float pPartialTick, PoseStack stack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        /*stack.pushPose();

        stack.translate(0.5, 0.5, 0.5);
        stack.mulPose(Axis.YP.rotationDegrees(pBlockEntity.getBlockPos().getY() % 2 == 0 ? 90 : 0));
        Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(Items.DIAMOND), ItemDisplayContext.NONE, pPackedLight, pPackedOverlay, stack, pBufferSource, null, 0);

        stack.translate(0, -0.3, 0.2);
        Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(Items.DIAMOND), ItemDisplayContext.NONE, pPackedLight, pPackedOverlay, stack, pBufferSource, null, 0);

        stack.popPose();*/
    }

    @Override
    public void renderSectionGeometry(HotpotTestBenchBlockEntity blockEntity, AddSectionGeometryEvent.SectionRenderingContext context, PoseStack stack, BlockPos blockPos, BlockPos regionOrigin, ISectionGeometryRenderContext modelRenderContext) {
        stack.pushPose();

        stack.translate(0.5, 0.5, 0.5);
        stack.mulPose(Axis.YP.rotationDegrees(blockPos.getY() % 2 == 0 ? 90 : 0));
        modelRenderContext.renderUncachedItem(new ItemStack(Items.DIAMOND), ItemDisplayContext.NONE, false, stack, OverlayTexture.NO_OVERLAY);

        stack.translate(0, -0.3, 0.2);
        modelRenderContext.renderUncachedItem(new ItemStack(Items.DIAMOND), ItemDisplayContext.NONE, false, stack, OverlayTexture.NO_OVERLAY);

        stack.popPose();
    }
}
