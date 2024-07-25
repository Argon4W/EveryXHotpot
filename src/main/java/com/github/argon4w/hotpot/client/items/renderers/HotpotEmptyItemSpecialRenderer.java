package com.github.argon4w.hotpot.client.items.renderers;

import com.github.argon4w.hotpot.client.items.IHotpotItemSpecialRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class HotpotEmptyItemSpecialRenderer implements IHotpotItemSpecialRenderer {
    @Override
    public void render(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

    }

    @Override
    public Optional<ModelResourceLocation> getDefaultItemModelResourceLocation() {
        return Optional.empty();
    }
}
