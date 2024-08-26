package com.github.argon4w.hotpot.client.contents.renderers;

import com.github.argon4w.hotpot.client.contents.HotpotItemContentSpecialRenderers;
import com.github.argon4w.hotpot.client.contents.IHotpotContentRenderer;
import com.github.argon4w.hotpot.client.contents.IHotpotItemContentSpecialRenderer;
import com.github.argon4w.hotpot.contents.AbstractHotpotItemStackContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class HotpotItemContentRenderer implements IHotpotContentRenderer {
    @Override
    public void render(IHotpotContent content, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, double rotation, double waterLevel, double x, double z) {
        if (!(content instanceof AbstractHotpotItemStackContent itemStackContent)) {
            return;
        }

        Item item = itemStackContent.getItemStack().getItem();
        ResourceLocation resourceLocation = BuiltInRegistries.ITEM.getKey(item);
        IHotpotItemContentSpecialRenderer itemContentSpecialRenderer = HotpotItemContentSpecialRenderers.getItemContentSpecialRenderer(resourceLocation);

        itemContentSpecialRenderer.render(itemStackContent, poseStack, bufferSource, combinedLight, combinedOverlay, waterLevel, rotation, x, z);
    }
}
