package com.github.argon4w.hotpot.client.soups.renderers;

import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.client.soups.IHotpotSoupCustomElementRenderer;
import com.github.argon4w.hotpot.client.soups.IHotpotSoupCustomElementRendererSerializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraftforge.client.model.data.ModelData;

public class HotpotEmptyCustomElementRenderer implements IHotpotSoupCustomElementRenderer {
    @Override
    public void render(BlockEntityRendererProvider.Context context, int time, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float renderedWaterLevel) {

    }

    @Override
    public boolean shouldRenderInBowl() {
        return false;
    }

    public static class Serializer implements IHotpotSoupCustomElementRendererSerializer<HotpotEmptyCustomElementRenderer> {
        @Override
        public HotpotEmptyCustomElementRenderer fromJson(JsonObject jsonObject) {
            return new HotpotEmptyCustomElementRenderer();
        }
    }
}
