package com.github.argon4w.hotpot.client.contents.renderers;

import com.github.argon4w.hotpot.api.client.contents.IHotpotContentRenderer;
import com.github.argon4w.hotpot.client.contents.player.HotpotPlayerModelRendererContext;
import com.github.argon4w.hotpot.client.contents.player.HotpotPlayerModelRendererContextCacheHolder;
import com.github.argon4w.hotpot.contents.HotpotPlayerContent;
import com.github.argon4w.hotpot.api.contents.IHotpotContent;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.component.ResolvableProfile;
import org.joml.Math;

import java.util.HashMap;

public class HotpotPlayerContentRenderer implements IHotpotContentRenderer {
    public static final HashMap<HotpotPlayerModelRendererContextCacheHolder, HotpotPlayerModelRendererContext> PLAYER_MODEL_RENDER_CONTEXT_CACHE = Maps.newHashMap();

    @Override
    public void render(IHotpotContent content, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, double rotation, double waterLevel, double x, double z, int index) {
        if (!(content instanceof HotpotPlayerContent playerContent)) {
            return;
        }

        int modelPartIndex = playerContent.getModelPartIndex();
        ResolvableProfile profile = playerContent.getProfile();

        HotpotPlayerModelRendererContext renderContext = HotpotPlayerContentRenderer.PLAYER_MODEL_RENDER_CONTEXT_CACHE.computeIfAbsent(new HotpotPlayerModelRendererContextCacheHolder(modelPartIndex, profile), holder -> new HotpotPlayerModelRendererContext(profile, modelPartIndex));

        if (!renderContext.isModelPartLoaded()) {
            renderContext.updateModelPartWithTexture();
        }

        double positionX = 0.5 + x * 0.325;
        double positionZ = 0.5 + z * 0.325;
        double positionY = 0.53 - curve(rotation / 360.0, 0) * 0.06 + 0.42 * waterLevel;
        double rotationY = curve(rotation / 360.0, 1) * 25.0;

        poseStack.pushPose();

        poseStack.translate(positionX, positionY, positionZ);

        poseStack.mulPose(Axis.YP.rotationDegrees((float) rotation));
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0f));
        poseStack.mulPose(Axis.XP.rotationDegrees((float) rotationY));

        poseStack.scale(0.25f, 0.25f, 0.25f);

        renderContext.getModelPart().render(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(renderContext.getModelPartTextureResourceLocation())), combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    public static double curve(double f, double offset) {
        return Math.sin((f + offset) / 0.25 * 2 * Math.PI);
    }
}
