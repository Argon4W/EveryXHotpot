package com.github.argon4w.hotpot.client.blocks;

import com.github.argon4w.hotpot.blocks.HotpotTestBenchBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
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

public class HotpotTestBenchBlockEntityRenderer implements BlockEntityRenderer<HotpotTestBenchBlockEntity>, ISectionGeometryBLockEntityRenderer<HotpotTestBenchBlockEntity> {
    public HotpotTestBenchBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(HotpotTestBenchBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

    }

    @Override
    public void renderSectionGeometry(HotpotTestBenchBlockEntity blockEntity, AddSectionGeometryEvent.SectionRenderingContext context, PoseStack stack, BlockPos blockPos, ModelRenderer modelRenderer) {
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(Blocks.DIRT.defaultBlockState());

        stack.pushPose();

        stack.translate(0.5, 0.5, 0.5);
        modelRenderer.renderSimpleItem(new ItemStack(Items.DIAMOND), ItemDisplayContext.NONE, stack, OverlayTexture.NO_OVERLAY);

        stack.popPose();
    }
}
