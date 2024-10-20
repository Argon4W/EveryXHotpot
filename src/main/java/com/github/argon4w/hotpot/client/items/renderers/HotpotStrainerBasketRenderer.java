package com.github.argon4w.hotpot.client.items.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.api.client.items.IHotpotItemSpecialRenderer;
import com.github.argon4w.hotpot.api.client.items.IHotpotStrainerBasketContentRenderer;
import com.github.argon4w.hotpot.client.contents.HotpotStrainerBasketContentRenderers;
import com.github.argon4w.hotpot.items.HotpotStrainerBasketItem;
import com.github.argon4w.hotpot.items.components.HotpotSkewerDataComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HotpotStrainerBasketRenderer implements IHotpotItemSpecialRenderer {
    @Override
    public void render(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        HotpotStrainerBasketItem.getStrainerBasketItems(itemStack).stream().collect(() -> new HashMap<IHotpotStrainerBasketContentRenderer, List<ItemStack>>(), (map, itemStack1) -> map.computeIfAbsent(HotpotStrainerBasketContentRenderers.getStrainerBasketContentRenderer(itemStack1), renderer -> new ArrayList<>()).add(itemStack1), (map1, map2) -> {}).forEach((renderer, itemStacks) -> renderer.renderAsItem(itemStacks, poseStack, bufferSource, combinedLight, combinedOverlay));
        poseStack.popPose();
    }

    @Override
    public Optional<ResourceLocation> getDefaultItemModelResourceLocation() {
        return Optional.of(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "item/hotpot_strainer_basket_model"));
    }
}
