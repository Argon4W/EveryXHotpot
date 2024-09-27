package com.github.argon4w.hotpot.client.sections;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public interface SectionGeometryRenderContext {
    void renderCachedModel(BakedModel model, PoseStack poseStack, RenderType renderType, int overlay, ModelData modelData);
    void renderCachedModel(BakedModel model, BlockState blockState, PoseStack poseStack, RenderType renderType, int overlay, ModelData modelData);
    void renderUncachedItem(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, int overlay);
    void renderUncachedItem(Level level, LivingEntity entity, int seed, ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, int overlay);
    int getPackedLight();
    MultiBufferSource getUncachedBufferSource();
    MultiBufferSource getUncachedItemBufferSource();
}
