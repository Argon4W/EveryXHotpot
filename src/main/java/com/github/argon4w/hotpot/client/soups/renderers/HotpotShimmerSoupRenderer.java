package com.github.argon4w.hotpot.client.soups.renderers;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.client.soups.IHotpotSoupCustomElementRenderer;
import com.github.argon4w.hotpot.client.soups.IHotpotSoupCustomElementRendererSerializer;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Math;

public class HotpotShimmerSoupRenderer implements IHotpotSoupCustomElementRenderer {
    @Override
    public void render(BlockEntityRendererProvider.Context context, int time, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float renderedWaterLevel) {
        poseStack.pushPose();
        poseStack.translate(0, 1, 0);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderInBowl() {
        return true;
    }

    public static class Serializer implements IHotpotSoupCustomElementRendererSerializer<HotpotShimmerSoupRenderer> {
        @Override
        public HotpotShimmerSoupRenderer fromJson(JsonObject jsonObject) {
            return new HotpotShimmerSoupRenderer();
        }
    }
}
