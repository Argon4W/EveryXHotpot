package com.github.argon4w.hotpot.client.items.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.SimpleItemSlot;
import com.github.argon4w.hotpot.client.items.IHotpotItemSpecialRenderer;
import com.github.argon4w.hotpot.items.HotpotNapkinHolderItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.Optional;

public class HotpotNapkinHolderItemRenderer implements IHotpotItemSpecialRenderer {
    @Override
    public void render(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        SimpleItemSlot napkinItemSlot = HotpotNapkinHolderItem.getNapkinItemSlot(itemStack);
        BakedModel napkinHolderModel = Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "item/hotpot_napkin_holder_model")));
        BakedModel napkinModel = Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "block/hotpot_napkin")));

        int color = DyedItemColor.getOrDefault(itemStack, -1);
        float r = FastColor.ARGB32.red(color) / 255.0f;
        float g = FastColor.ARGB32.green(color) / 255.0f;
        float b = FastColor.ARGB32.blue(color) / 255.0f;

        poseStack.pushPose();
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), null, napkinHolderModel, r, g, b, combinedLight, combinedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());
        poseStack.popPose();

        if (napkinItemSlot.isEmpty()) {
            return;
        }

        for (int i = 0; i < napkinItemSlot.getRenderCount(); i ++) {
            poseStack.pushPose();

            poseStack.translate(0.5f, 0.0625f + 0.05f * i, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees((i % 2 == 0 ? 1 : -1) * 3));

            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), null, napkinModel, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());

            poseStack.popPose();
        }
    }

    @Override
    public Optional<ResourceLocation> getDefaultItemModelResourceLocation() {
        return Optional.empty();
    }
}
