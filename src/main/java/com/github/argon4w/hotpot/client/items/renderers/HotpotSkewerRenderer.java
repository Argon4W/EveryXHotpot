package com.github.argon4w.hotpot.client.items.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.api.client.items.IHotpotItemSpecialRenderer;
import com.github.argon4w.hotpot.client.MappingBufferSource;
import com.github.argon4w.hotpot.items.HotpotSkewerItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class HotpotSkewerRenderer implements IHotpotItemSpecialRenderer {
    @Override
    public void render(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (HotpotSkewerItem.isSkewerEmpty(itemStack)) {
            return;
        }

        List<ItemStack> skewerItems = HotpotSkewerItem.getSkewerItems(itemStack);

        for (int i = 0; i < Math.min(3, skewerItems.size()); i ++) {
            ItemStack skewerItemStack = skewerItems.get(skewerItems.size() - 1 - i);

            float rotationZ = i % 2 == 0 ? 15.0f : -15.0f;
            float positionY = 0.68f + 0.44f * i;

            poseStack.pushPose();

            poseStack.translate(0.5f, positionY, 0.5f);
            poseStack.mulPose(Axis.XP.rotationDegrees(15.0f));
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotationZ));
            poseStack.scale(0.7f, 0.7f, 1.6f);

            Minecraft.getInstance().getItemRenderer().renderStatic(null, skewerItemStack, ItemDisplayContext.NONE, true, poseStack, MappingBufferSource.itemBufferSource(bufferSource), null, combinedLight, combinedOverlay, 42);
            poseStack.popPose();
        }
    }

    @Override
    public Optional<ResourceLocation> getDefaultItemModelResourceLocation() {
        return Optional.of(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "item/hotpot_skewer_model"));
    }
}
