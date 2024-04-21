package com.github.argon4w.hotpot.client.items.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import com.github.argon4w.hotpot.client.items.IHotpotItemSpecialRenderer;
import com.github.argon4w.hotpot.items.HotpotSpicePackItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class HotpotSpicePackRenderer implements IHotpotItemSpecialRenderer {
    @Override
    public void render(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (!HotpotTagsHelper.hasHotpotTags(itemStack)) {
            return;
        }

        List<ItemStack> itemStacks = HotpotSpicePackItem.getSpicePackItems(itemStack);
        float startX = 0.3f - (0.3f / (itemStacks.size() * 3f)) * Math.max(0, itemStacks.size() - 1);

        poseStack.pushPose();

        poseStack.translate(startX + 0.2f, 0.25f, 0.5f);

        for (ItemStack spiceItemStack : itemStacks) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(30f));
            poseStack.scale(0.78f, 0.78f, 0.78f);

            Minecraft.getInstance().getItemRenderer().renderStatic(null, spiceItemStack, ItemDisplayContext.GROUND, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemDisplayContext.FIXED.ordinal());

            poseStack.popPose();

            poseStack.translate(0.3f / (itemStacks.size() * 1.5f), 0, 0);
        }

        poseStack.popPose();
    }

    @Override
    public Optional<ResourceLocation> getDefaultItemModelResourceLocation() {
        return Optional.of(new ResourceLocation(HotpotModEntry.MODID, "item/hotpot_spice_pack_model"));
    }
}
