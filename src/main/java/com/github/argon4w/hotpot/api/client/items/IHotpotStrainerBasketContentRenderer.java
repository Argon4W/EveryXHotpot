package com.github.argon4w.hotpot.api.client.items;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;

public interface IHotpotStrainerBasketContentRenderer {

    void renderInSoup(
            List<ItemStack> itemStacks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int combinedLight,
            int combinedOverlay,
            int contentIndex,
            double waterLevel,
            double maxHeight,
            double time);

    void renderAsItem(
            List<ItemStack> itemStacks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int combinedLight,
            int combinedOverlay);
}
