package com.github.argon4w.hotpot.client.items.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import com.github.argon4w.hotpot.client.items.IHotpotItemSpecialRenderer;
import com.github.argon4w.hotpot.items.HotpotSkewerItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

public class HotpotSkewerRenderer implements IHotpotItemSpecialRenderer {
    @Override
    public void render(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (!HotpotTagsHelper.hasHotpotTags(itemStack)) {
            return;
        }

        List<ItemStack> skewerItems = HotpotSkewerItem.getSkewerItems(itemStack);

        RandomSource randomSource = RandomSource.create();
        randomSource.setSeed(42);

        for (int i = 0; i < Math.min(3, skewerItems.size()); i ++) {
            ItemStack skewerItemStack = skewerItems.get(skewerItems.size() - 1 - i);

            poseStack.pushPose();

            poseStack.translate(0.5f, 0.68f + 0.44f * i, 0.5f);
            poseStack.mulPose(Axis.XP.rotationDegrees(i % 2 == 0 ? 15.0f : -15.0f));
            poseStack.scale(0.7f, 0.7f, 0.7f);

            Minecraft.getInstance().getItemRenderer().renderStatic(null, skewerItemStack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemDisplayContext.FIXED.ordinal());
            poseStack.popPose();
        }
    }

    @Override
    public Optional<ResourceLocation> getDefaultItemModelResourceLocation() {
        return Optional.of(new ResourceLocation(HotpotModEntry.MODID, "item/hotpot_skewer_model"));
    }
}
