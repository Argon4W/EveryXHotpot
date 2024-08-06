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

public class HotpotChopstickRenderer implements IHotpotItemSpecialRenderer {
    @Override
    public void render(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        ItemStack chopstickFoodItemStack;
        if ((chopstickFoodItemStack = HotpotChopstickItem.getHeldItemStack(itemStack)).isEmpty()) {
            return;
        }

        poseStack.pushPose();

        poseStack.translate(0.5f, 0.1f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(90f));
        Minecraft.getInstance().getItemRenderer().renderStatic(null, chopstickFoodItemStack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemDisplayContext.FIXED.ordinal());

        poseStack.popPose();
    }

    @Override
    public Optional<ResourceLocation> getDefaultItemModelResourceLocation() {
        return Optional.of(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "item/hotpot_chopstick_model"));
    }
}
