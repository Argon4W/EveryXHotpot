package com.github.argon4w.hotpot.api.client.items;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IHotpotStrainerBasketContentRenderer {
    void renderInSoup(List<ItemStack> itemStacks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, int contentIndex, double waterLevel, double maxHeight, double time);
    void renderAsItem(List<ItemStack> itemStacks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay);
}
