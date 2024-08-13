package com.github.argon4w.hotpot.client.soups.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Math;

import java.util.List;

public class HotpotSoupFloatingElementRenderer implements IHotpotSoupCustomElementRenderer {
    private final float rotationTimeOffset;
    private final float positionTimeOffset;
    private final float rotationScale;
    private final float positionScale;
    private final float positionOffset;
    private final RotationAxis rotationAxis;

    private final ResourceLocation elementModelResourceLocation;
    private final boolean shouldRenderInBowl;

    private BakedModel model;

    public HotpotSoupFloatingElementRenderer(float rotationTimeOffset, float positionTimeOffset, float rotationScale, float positionScale, float positionOffset, RotationAxis rotationAxis, ResourceLocation elementModelResourceLocation, boolean shouldRenderInBowl) {
        this.rotationTimeOffset = rotationTimeOffset;
        this.positionTimeOffset = positionTimeOffset;
        this.rotationScale = rotationScale;
        this.positionScale = positionScale;
        this.positionOffset = positionOffset;
        this.rotationAxis = rotationAxis;

        this.elementModelResourceLocation = elementModelResourceLocation;
        this.shouldRenderInBowl = shouldRenderInBowl;
    }

    @Override
    public void prepareModel() {
        model = Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(elementModelResourceLocation));
    }

    @Override
    public void render(long time, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float waterLevel) {
        if (model == null) {
            return;
        }

        float t = time / 20f / 5f;
        float y = waterLevel * 0.4375f + 0.5625f;

        float rotation = Math.sin(rotationTimeOffset + t * (float) Math.PI) * rotationScale;
        float position = Math.cos(positionTimeOffset + t * (float) Math.PI) * positionScale + y + positionOffset;

        poseStack.pushPose();
        poseStack.translate(0f, position, 0f);
        poseStack.mulPose(rotationAxis.getAxis().rotationDegrees(rotation));

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.translucentCullBlockSheet()), null, model, 1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, Sheets.translucentCullBlockSheet());

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderInBowl() {
        return shouldRenderInBowl;
    }

    @Override
    public List<ResourceLocation> getRequiredModelResourceLocations() {
        return List.of(elementModelResourceLocation);
    }

    @Override
    public Holder<IHotpotSoupCustomElementRendererSerializer<?>> getSerializer() {
        return HotpotSoupCustomElementSerializers.FLOATING_ELEMENT_RENDERER_SERIALIZER;
    }

    public float getRotationTimeOffset() {
        return rotationTimeOffset;
    }

    public float getPositionTimeOffset() {
        return positionTimeOffset;
    }

    public float getRotationScale() {
        return rotationScale;
    }

    public float getPositionScale() {
        return positionScale;
    }

    public float getPositionOffset() {
        return positionOffset;
    }

    public RotationAxis getRotationAxis() {
        return rotationAxis;
    }

    public ResourceLocation getElementModelResourceLocation() {
        return elementModelResourceLocation;
    }

    public enum RotationAxis implements StringRepresentable {
        X(Axis.XP, "x"), Y(Axis.YP, "y"), Z(Axis.ZP, "z");

        private final Axis axis;
        private final String name;

        RotationAxis(Axis axis, String name) {
            this.axis = axis;
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        public Axis getAxis() {
            return axis;
        }
    }

    public static class Serializer implements IHotpotSoupCustomElementRendererSerializer<HotpotSoupFloatingElementRenderer> {
        public static final MapCodec<HotpotSoupFloatingElementRenderer> CODEC = RecordCodecBuilder.mapCodec(renderer -> renderer.group(
                Codec.FLOAT.fieldOf("rotation_time_offset").forGetter(HotpotSoupFloatingElementRenderer::getRotationTimeOffset),
                Codec.FLOAT.fieldOf("position_time_offset").forGetter(HotpotSoupFloatingElementRenderer::getPositionTimeOffset),
                Codec.FLOAT.fieldOf("rotation_scale").forGetter(HotpotSoupFloatingElementRenderer::getRotationScale),
                Codec.FLOAT.fieldOf("position_scale").forGetter(HotpotSoupFloatingElementRenderer::getPositionScale),
                Codec.FLOAT.fieldOf("position_offset").forGetter(HotpotSoupFloatingElementRenderer::getPositionOffset),
                StringRepresentable.fromEnum(RotationAxis::values).fieldOf("rotation_axis").forGetter(HotpotSoupFloatingElementRenderer::getRotationAxis),
                ResourceLocation.CODEC.fieldOf("element_model_resource_location").forGetter(HotpotSoupFloatingElementRenderer::getElementModelResourceLocation),
                Codec.BOOL.fieldOf("should_render_in_bowl").forGetter(HotpotSoupFloatingElementRenderer::shouldRenderInBowl)
        ).apply(renderer, HotpotSoupFloatingElementRenderer::new));

        @Override
        public MapCodec<HotpotSoupFloatingElementRenderer> getCodec() {
            return CODEC;
        }
    }
}
