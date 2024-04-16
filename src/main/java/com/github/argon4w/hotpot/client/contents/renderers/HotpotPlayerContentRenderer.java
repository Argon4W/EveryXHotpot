package com.github.argon4w.hotpot.client.contents.renderers;

import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.client.contents.player.HotpotPlayerModelRenderContext;
import com.github.argon4w.hotpot.client.contents.IHotpotContentRenderer;
import com.github.argon4w.hotpot.contents.HotpotPlayerContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.joml.Math;

import java.util.HashMap;

public class HotpotPlayerContentRenderer implements IHotpotContentRenderer {
    public static final HashMap<HotpotPlayerContent, HotpotPlayerModelRenderContext> MODEL_RENDER_CONTEXTS = Maps.newHashMap();
    public static final float ITEM_ROUND_TRIP_TIME = 60f;
    public static final float ITEM_RADIUS = 0.325f;
    public static final float ITEM_START_Y = 0.53f;
    public static final float ITEM_FLOAT_Y = 0.06f;
    public static final float ITEM_ROTATION = 25f;
    public static final float ITEM_SCALE = 0.25f;

    @Override
    public void render(IHotpotContent content, BlockEntityRendererProvider.Context context, HotpotBlockEntity hotpotBlockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float offset, float waterline) {
        if (!(content instanceof HotpotPlayerContent playerContent)) {
            return;
        }

        HotpotPlayerModelRenderContext renderContext = HotpotPlayerContentRenderer.MODEL_RENDER_CONTEXTS.computeIfAbsent(playerContent, p -> new HotpotPlayerModelRenderContext(p.getProfile(), p.getPartIndex()));

        if (!renderContext.isModelPartLoaded()) {
            renderContext.updateModelPartWithTexture();
        }

        poseStack.pushPose();

        float f = hotpotBlockEntity.getTime() / 20f / ITEM_ROUND_TRIP_TIME + offset;

        poseStack.translate(0.5f + Math.sin(f * 2f * Math.PI) * ITEM_RADIUS, ITEM_START_Y + getFloatingCurve(f, 0f) * ITEM_FLOAT_Y + 0.42f * waterline, 0.5f + Math.cos(f * 2f * Math.PI) * ITEM_RADIUS);
        poseStack.mulPose(Axis.YP.rotationDegrees(f * 360f));
        poseStack.mulPose(Axis.XP.rotationDegrees(-90f + getFloatingCurve(f, 1f) * ITEM_ROTATION));
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

        renderContext.getModelPart().render(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(renderContext.getModelPartTextureResourceLocation())), combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    public static float getFloatingCurve(float f, float offset) {
        return (float) Math.sin((f + offset) / 0.25f * 2f * Math.PI);
    }
}
