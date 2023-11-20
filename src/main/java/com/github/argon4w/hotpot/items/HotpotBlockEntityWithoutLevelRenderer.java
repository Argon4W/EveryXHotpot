package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.util.Constants;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HotpotBlockEntityWithoutLevelRenderer extends ItemStackTileEntityRenderer {

    @Override
    public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType displayContext, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay) {
        if (itemStack.getItem().equals(HotpotModEntry.HOTPOT_CHOPSTICK.get())) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.5f, 0.5f);

            IBakedModel chopstickModel = Minecraft.getInstance().getModelManager().getModel(new ResourceLocation(HotpotModEntry.MODID, "item/hotpot_chopstick_model"));
            Minecraft.getInstance().getItemRenderer().render(itemStack, displayContext, true, poseStack, bufferSource, combinedLight, combinedOverlay, chopstickModel);

            poseStack.popPose();

            ItemStack chopstickFoodItemStack;
            if (!(chopstickFoodItemStack = HotpotChopstickItem.getChopstickFoodItemStack(itemStack)).isEmpty()) {
                poseStack.pushPose();

                poseStack.translate(0.5f, 0.1f, 0.5f);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(90f));
                Minecraft.getInstance().getItemRenderer().renderStatic(null, chopstickFoodItemStack, ItemCameraTransforms.TransformType.FIXED, true, poseStack, bufferSource, null, combinedLight, combinedOverlay);

                poseStack.popPose();
            }
        } else if (itemStack.getItem().equals(HotpotModEntry.HOTPOT_SPICE_PACK.get())) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.5f, 0.5f);

            IBakedModel spicePackModel = Minecraft.getInstance().getModelManager().getModel(new ResourceLocation(HotpotModEntry.MODID, "item/hotpot_spice_pack_model"));
            Minecraft.getInstance().getItemRenderer().render(itemStack, displayContext, true, poseStack, bufferSource, combinedLight, combinedOverlay, spicePackModel);

            poseStack.popPose();

            poseStack.pushPose();

            List<ItemStack> itemStacks = HotpotTagsHelper.hasHotpotTag(itemStack) ?
                    HotpotTagsHelper.getHotpotTag(itemStack).getList("Spices", Constants.NBT.TAG_COMPOUND).stream().map(tag -> ItemStack.of((CompoundNBT) tag)).collect(Collectors.toList())
                    : new ArrayList<>();
            float startX = 0.3f - (0.3f / (itemStacks.size() * 3f)) * Math.max(0, itemStacks.size() - 1);

            poseStack.translate(startX + 0.2f, 0.25f, 0.5f);

            for (ItemStack spiceItemStack : itemStacks) {
                poseStack.pushPose();
                poseStack.mulPose(Vector3f.YP.rotationDegrees(30f));
                poseStack.scale(0.78f, 0.78f, 0.78f);

                Minecraft.getInstance().getItemRenderer().renderStatic(null, spiceItemStack, ItemCameraTransforms.TransformType.GROUND, true, poseStack, bufferSource, null, combinedLight, combinedOverlay);

                poseStack.popPose();

                poseStack.translate(0.3f / (itemStacks.size() * 1.5f), 0, 0);
            }

            poseStack.popPose();
        }
    }
}
