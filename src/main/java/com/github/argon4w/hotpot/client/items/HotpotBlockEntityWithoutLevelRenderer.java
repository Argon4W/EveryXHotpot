package com.github.argon4w.hotpot.client.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import com.github.argon4w.hotpot.client.blocks.HotpotBlockEntityRenderer;
import com.github.argon4w.hotpot.client.contents.HotpotContentRenderers;
import com.github.argon4w.hotpot.client.soups.HotpotSoupRendererConfig;
import com.github.argon4w.hotpot.client.soups.IHotpotSoupCustomElementRenderer;
import com.github.argon4w.hotpot.contents.HotpotCookingRecipeContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.items.HotpotChopstickItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        ResourceLocation itemResourceLocation = ForgeRegistries.ITEMS.getKey(item);
        IHotpotItemSpecialRenderer renderer = HotpotItemSpecialRenderers.getItemSpecialRenderer(itemResourceLocation);

        renderer.getDefaultItemModelResourceLocation().ifPresent(resourceLocation -> {
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.5f, 0.5f);

            BakedModel chopstickModel = Minecraft.getInstance().getModelManager().getModel(resourceLocation);
            Minecraft.getInstance().getItemRenderer().render(itemStack, displayContext, true, poseStack, bufferSource, combinedLight, combinedOverlay, chopstickModel);

            poseStack.popPose();
        });

        renderer.render(itemStack, displayContext, poseStack, bufferSource, combinedLight, combinedOverlay);
    }
}
