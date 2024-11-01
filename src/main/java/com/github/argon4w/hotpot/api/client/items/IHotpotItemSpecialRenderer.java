package com.github.argon4w.hotpot.api.client.items;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Optional;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public interface IHotpotItemSpecialRenderer {
    void render(
            ItemStack itemStack,
            ItemDisplayContext displayContext,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int combinedLight,
            int combinedOverlay);

    Optional<ResourceLocation> getItemModelResourceLocation();
}
