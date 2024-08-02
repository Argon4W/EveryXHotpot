package com.github.argon4w.hotpot.client.items.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.items.IHotpotItemSpecialRenderer;
import com.github.argon4w.hotpot.items.HotpotChopstickItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class HotpotNapkinHolderRenderer implements IHotpotItemSpecialRenderer {
    @Override
    public void render(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

    }

    @Override
    public Optional<ResourceLocation> getDefaultItemModelResourceLocation() {
        return Optional.of(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "item/hotpot_napkin_holder_model"));
    }
}
