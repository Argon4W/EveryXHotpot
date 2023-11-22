package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HotpotBlockEntityWithoutLevelRenderer extends BlockEntityWithoutLevelRenderer {
    public HotpotBlockEntityWithoutLevelRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (itemStack.is(HotpotModEntry.HOTPOT_CHOPSTICK.get())) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.5f, 0.5f);

            BakedModel chopstickModel = Minecraft.getInstance().getModelManager().getModel(new ResourceLocation(HotpotModEntry.MODID, "item/hotpot_chopstick_model"));
            Minecraft.getInstance().getItemRenderer().render(itemStack, displayContext, true, poseStack, bufferSource, combinedLight, combinedOverlay, chopstickModel);

            poseStack.popPose();

            ItemStack chopstickFoodItemStack;
            if (!(chopstickFoodItemStack = HotpotChopstickItem.getChopstickFoodItemStack(itemStack)).isEmpty()) {
                poseStack.pushPose();

                poseStack.translate(0.5f, 0.1f, 0.5f);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(90f));
                Minecraft.getInstance().getItemRenderer().renderStatic(null, chopstickFoodItemStack, ItemTransforms.TransformType.FIXED, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemTransforms.TransformType.FIXED.ordinal());

                poseStack.popPose();
            }
        } else if (itemStack.is(HotpotModEntry.HOTPOT_SPICE_PACK.get())) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.5f, 0.5f);

            BakedModel spicePackModel = Minecraft.getInstance().getModelManager().getModel(new ResourceLocation(HotpotModEntry.MODID, "item/hotpot_spice_pack_model"));
            Minecraft.getInstance().getItemRenderer().render(itemStack, displayContext, true, poseStack, bufferSource, combinedLight, combinedOverlay, spicePackModel);

            poseStack.popPose();

            poseStack.pushPose();

            List<ItemStack> itemStacks = HotpotTagsHelper.hasHotpotTag(itemStack) ?
                    HotpotTagsHelper.getHotpotTag(itemStack).getList("Spices", Tag.TAG_COMPOUND).stream().map(tag -> ItemStack.of((CompoundTag) tag)).collect(Collectors.toList())
                    : new ArrayList<>();
            float startX = 0.3f - (0.3f / (itemStacks.size() * 3f)) * Math.max(0, itemStacks.size() - 1);

            poseStack.translate(startX + 0.2f, 0.25f, 0.5f);

            for (ItemStack spiceItemStack : itemStacks) {
                poseStack.pushPose();
                poseStack.mulPose(Vector3f.YP.rotationDegrees(30f));
                poseStack.scale(0.78f, 0.78f, 0.78f);

                Minecraft.getInstance().getItemRenderer().renderStatic(null, spiceItemStack, ItemTransforms.TransformType.GROUND, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemTransforms.TransformType.FIXED.ordinal());

                poseStack.popPose();

                poseStack.translate(0.3f / (itemStacks.size() * 1.5f), 0, 0);
            }

            poseStack.popPose();
        }
    }
}
