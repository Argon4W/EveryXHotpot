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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.neoforged.neoforge.client.model.data.ModelData;

public class HotpotTestBenchBlockEntityRenderer implements BlockEntityRenderer<HotpotTestBenchBlockEntity>, IHotpotSectionGeometryBLockEntityRenderer<HotpotTestBenchBlockEntity> {
    public HotpotTestBenchBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(HotpotTestBenchBlockEntity pBlockEntity, float pPartialTick, PoseStack stack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        /*stack.pushPose();

        stack.translate(0.5, 0.5, 0.5);
        stack.mulPose(Axis.YP.rotationDegrees(45));
        Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(Items.DIAMOND), ItemDisplayContext.NONE, pPackedLight, pPackedOverlay, stack, pBufferSource, null, 0);

        stack.popPose();*/
    }

    @Override
    public void renderSectionGeometry(HotpotTestBenchBlockEntity blockEntity, AddSectionGeometryEvent.SectionRenderingContext context, PoseStack stack, BlockPos blockPos, ModelRenderer modelRenderer) {
        stack.pushPose();

        stack.translate(0.5, 0.5, 0.5);
        stack.mulPose(Axis.YP.rotationDegrees(blockPos.getY() % 2 == 0 ? 90 : 0));
        modelRenderer.renderItem(new ItemStack(Items.DIAMOND), ItemDisplayContext.NONE, false, stack, OverlayTexture.NO_OVERLAY);

        stack.translate(0, -0.3, 0.2);
        modelRenderer.renderItem(new ItemStack(Items.DIAMOND), ItemDisplayContext.NONE, false, stack, OverlayTexture.NO_OVERLAY);

        stack.popPose();
    }
}
