package com.github.argon4w.hotpot.client.items.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import com.github.argon4w.hotpot.client.blocks.HotpotBlockEntityRenderer;
import com.github.argon4w.hotpot.client.items.IHotpotItemSpecialRenderer;
import com.github.argon4w.hotpot.client.items.process.HotpotSpriteProcessors;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.model.data.ModelData;

import java.util.List;
import java.util.Optional;

public class HotpotPaperBowlRenderer implements IHotpotItemSpecialRenderer {
    @Override
    public void render(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        poseStack.pushPose();

        float scale = 9.0f / 16.0f;
        float step = 3.5f / 16.0f;
        float pivot = step / scale;
        boolean hasSoup = itemStack.hasTag() ? itemStack.getTag().getBoolean("HasSoup") : true;

        RandomSource randomSource = RandomSource.create();
        randomSource.setSeed(42);

        poseStack.scale(scale, scale, scale);
        poseStack.translate(pivot, 0, pivot);

        poseStack.pushPose();

        List<ItemStack> itemStacks = List.of(new ItemStack(Items.COOKED_BEEF), new ItemStack(Items.COOKED_COD), new ItemStack(Items.COOKED_MUTTON), new ItemStack(Items.COOKED_CHICKEN), new ItemStack(Items.COOKED_BEEF), new ItemStack(Items.COOKED_COD), new ItemStack(Items.COOKED_MUTTON), new ItemStack(Items.COOKED_CHICKEN));

        for (int i = 0; i < 8; i ++) {
            ItemStack beef = itemStacks.get(i);

            HotpotTagsHelper.updateHotpotTag(beef, compoundTag -> compoundTag.putString("Soup", "everyxhotpot:spicy_soup"));
            HotpotSpriteProcessors.applyProcessor(new ResourceLocation(HotpotModEntry.MODID, "light_sauced_processor"), beef);

            float startOffset = (i % 2 == 0) ? 0.195f : -0.195f;
            float position = 0.08f * i;

            poseStack.pushPose();

            poseStack.translate(0.19f, hasSoup ? 0.7f : 0.6f, 0.5f);
            poseStack.translate(position, 0.0f, hasSoup ? startOffset : - startOffset);

            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.mulPose(Axis.XN.rotationDegrees(65));
            poseStack.mulPose(Axis.YP.rotationDegrees((i % 2 == 0) ? 180f : 0.0f + (randomSource.nextInt(0, 21) - 10)));
            poseStack.mulPose(Axis.ZP.rotationDegrees(hasSoup ? 180.0f : 0.0f + (randomSource.nextInt(0, 21) - 10)));

            poseStack.scale(0.6f, 0.6f, 0.6f);

            Minecraft.getInstance().getItemRenderer().renderStatic(null, beef, ItemDisplayContext.FIXED, true, poseStack, bufferSource, null, combinedLight, combinedOverlay, ItemDisplayContext.FIXED.ordinal());

            poseStack.popPose();
        }

        float renderedWaterLevel = hasSoup ? 0.5f : 0.01f;
        ResourceLocation soupResourceLocation = new ResourceLocation(HotpotModEntry.MODID, "tomato_soup");

        BlockEntityRendererProvider.Context context = new BlockEntityRendererProvider.Context(
                Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getBlockRenderer(),
                Minecraft.getInstance().getItemRenderer(),
                Minecraft.getInstance().getEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels(),
                Minecraft.getInstance().font
        );

        HotpotBlockEntityRenderer.renderHotpotSoup(
                context,
                poseStack,
                bufferSource,
                soupResourceLocation,
                50,
                0,
                combinedLight,
                combinedOverlay,
                renderedWaterLevel,
                true,
                true
        );

        HotpotBlockEntityRenderer.renderHotpotSoup(
                context,
                poseStack,
                bufferSource,
                soupResourceLocation,
                50,
                0,
                combinedLight,
                combinedOverlay,
                0,
                true,
                false
        );

        if (!hasSoup) {
            poseStack.pushPose();
            poseStack.translate(0, renderedWaterLevel * 0.4375f + 0.579f, 0);

            ResourceLocation condimentsResourceLocation = new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_packed_hotpot_condiments");
            BakedModel model = context.getBlockRenderDispatcher().getBlockModelShaper().getModelManager().getModel(condimentsResourceLocation);
            context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.translucent()), null, model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, RenderType.translucent());

            poseStack.popPose();
        }

        poseStack.popPose();

        poseStack.popPose();
    }

    @Override
    public Optional<ResourceLocation> getDefaultItemModelResourceLocation() {
        return Optional.of(new ResourceLocation(HotpotModEntry.MODID, "item/hotpot_paper_bowl_model"));
    }
}
