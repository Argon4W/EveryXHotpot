package com.github.argon4w.hotpot.client.contents.renderers;

import com.github.argon4w.hotpot.client.contents.IHotpotContentRenderer;
import com.github.argon4w.hotpot.client.contents.player.HotpotPlayerModelRenderContext;
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

    @Override
    public void render(IHotpotContent content, BlockEntityRendererProvider.Context context, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float rotation, float waterLevel, float x, float z) {
        if (!(content instanceof HotpotPlayerContent playerContent)) {
            return;
        }

        HotpotPlayerModelRenderContext renderContext = HotpotPlayerContentRenderer.MODEL_RENDER_CONTEXTS.computeIfAbsent(playerContent, p -> new HotpotPlayerModelRenderContext(p.getProfile(), p.getModelPartIndex()));

        if (!renderContext.isModelPartLoaded()) {
            renderContext.updateModelPartWithTexture();
        }

        poseStack.pushPose();

        poseStack.translate(0.5f + x * 0.325f, 0.53f - getFloatingCurve(rotation / 360.0f, 0f) * 0.06f + 0.42f * waterLevel, 0.5f + z * 0.325f);

        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.mulPose(Axis.XP.rotationDegrees( 90.0f));
        poseStack.mulPose(Axis.XP.rotationDegrees(getFloatingCurve(rotation / 360.0f, 1f) * 25.0f));

        poseStack.scale(0.25f, 0.25f, 0.25f);

        renderContext.getModelPart().render(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(renderContext.getModelPartTextureResourceLocation())), combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    public static float getFloatingCurve(float f, float offset) {
        return (float) Math.sin((f + offset) / 0.25f * 2f * Math.PI);
    }
}
