package com.github.argon4w.hotpot.client.items;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class HotpotBlockEntityWithoutLevelRenderer extends BlockEntityWithoutLevelRenderer {
    public HotpotBlockEntityWithoutLevelRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (itemStack.isEmpty()) {
            return;
        }

        Item item = itemStack.getItem();
        ResourceLocation itemResourceLocation = BuiltInRegistries.ITEM.getKey(item);
        IHotpotItemSpecialRenderer renderer = HotpotItemSpecialRenderers.getItemSpecialRenderer(itemResourceLocation);

        renderer.getDefaultItemModelResourceLocation().ifPresent(resourceLocation -> {
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.5f, 0.5f);

            BakedModel chopstickModel = Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(resourceLocation));
            Minecraft.getInstance().getItemRenderer().render(itemStack, displayContext, true, poseStack, bufferSource, combinedLight, combinedOverlay, chopstickModel);

            poseStack.popPose();
        });

        renderer.render(itemStack, displayContext, poseStack, bufferSource, combinedLight, combinedOverlay);
    }
}
