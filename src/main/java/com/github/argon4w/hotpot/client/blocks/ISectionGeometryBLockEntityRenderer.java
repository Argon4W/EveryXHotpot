package com.github.argon4w.hotpot.client.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.neoforged.neoforge.client.model.data.ModelData;

public interface ISectionGeometryBLockEntityRenderer<T extends BlockEntity> {
    void renderSectionGeometry(T blockEntity, AddSectionGeometryEvent.SectionRenderingContext context, PoseStack poseStack, BlockPos pos, ModelRenderer modelRenderer);

    interface ModelRenderer {
        void renderModel(BakedModel model, PoseStack stack, RenderType renderType, int overlay, ModelData modelData);
        void renderModel(BakedModel model, BlockState blockState, PoseStack stack, RenderType renderType, int overlay, ModelData modelData);
        void renderSimpleItem(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, int overlay);
        void renderSimpleItem(Level level, LivingEntity entity, int seed, ItemDisplayContext displayContext, PoseStack poseStack, int overlay, ItemStack itemStack);
    }
}
