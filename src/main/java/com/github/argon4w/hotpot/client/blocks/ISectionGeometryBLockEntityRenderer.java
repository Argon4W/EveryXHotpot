package com.github.argon4w.hotpot.client.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.neoforged.neoforge.client.model.data.ModelData;

public interface ISectionGeometryBLockEntityRenderer {
    void renderSectionGeometry(AddSectionGeometryEvent.SectionRenderingContext context, PoseStack stack, BlockPos blockPos, ModelRenderer modelRenderer);

    interface ModelRenderer {
        void renderModel(BakedModel model, PoseStack stack, RenderType renderType, int overlay, ModelData modelData);
        void renderModel(BakedModel model, BlockState blockState, PoseStack stack, RenderType renderType, int overlay, ModelData modelData);
    }
}
