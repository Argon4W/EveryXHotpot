package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class HotpotBlockEntityWithoutLevelRenderer extends BlockEntityWithoutLevelRenderer {
    public HotpotBlockEntityWithoutLevelRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);

        BakedModel chopstickModel = Minecraft.getInstance().getModelManager().getModel(new ResourceLocation(HotpotModEntry.MODID, "item/hotpot_chopstick_model"));
        Minecraft.getInstance().getItemRenderer().render(itemStack, displayContext, true, poseStack, bufferSource, combinedLight, combinedOverlay, chopstickModel);

        poseStack.popPose();

        ItemStack chopstickFoodItemStack;
        if (!(chopstickFoodItemStack = HotpotChopstickItem.getChopstickFoodItemStack(itemStack)).isEmpty()) {
            poseStack.pushPose();

            poseStack.translate(0.5f, 0.1f, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees(90f));
            Minecraft.getInstance().getItemRenderer().renderStatic(null, chopstickFoodItemStack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemDisplayContext.FIXED.ordinal());

            poseStack.popPose();
        }
    }
}
