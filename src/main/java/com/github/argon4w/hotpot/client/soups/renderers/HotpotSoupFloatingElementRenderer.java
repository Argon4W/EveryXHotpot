package com.github.argon4w.hotpot.client.soups.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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
    private final double rotationTimeOffset;
    private final double positionTimeOffset;
    private final double rotationScale;
    private final double positionScale;
    private final double positionOffset;
    private final RotationAxis rotationAxis;

    private final ResourceLocation elementModelResourceLocation;
    private final boolean shouldRenderInBowl;

    private BakedModel model;

    public HotpotSoupFloatingElementRenderer(double rotationTimeOffset, double positionTimeOffset, double rotationScale, double positionScale, double positionOffset, RotationAxis rotationAxis, ResourceLocation elementModelResourceLocation, boolean shouldRenderInBowl) {
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
    public void render(long time, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, double waterLevel) {
        if (model == null) {
            return;
        }

        double t = time / 20.0 / 5.0;
        double y = waterLevel * 0.4375 + 0.5625;

        double rotation = Math.sin(rotationTimeOffset + t * (float) Math.PI) * rotationScale;
        double position = Math.cos(positionTimeOffset + t * (float) Math.PI) * positionScale + y + positionOffset;

        poseStack.pushPose();
        poseStack.translate(0, position, 0);
        poseStack.mulPose(rotationAxis.getAxis().rotationDegrees((float) rotation));

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

    public double getRotationTimeOffset() {
        return rotationTimeOffset;
    }

    public double getPositionTimeOffset() {
        return positionTimeOffset;
    }

    public double getRotationScale() {
        return rotationScale;
    }

    public double getPositionScale() {
        return positionScale;
    }

    public double getPositionOffset() {
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
                Codec.DOUBLE.fieldOf("rotation_time_offset").forGetter(HotpotSoupFloatingElementRenderer::getRotationTimeOffset),
                Codec.DOUBLE.fieldOf("position_time_offset").forGetter(HotpotSoupFloatingElementRenderer::getPositionTimeOffset),
                Codec.DOUBLE.fieldOf("rotation_scale").forGetter(HotpotSoupFloatingElementRenderer::getRotationScale),
                Codec.DOUBLE.fieldOf("position_scale").forGetter(HotpotSoupFloatingElementRenderer::getPositionScale),
                Codec.DOUBLE.fieldOf("position_offset").forGetter(HotpotSoupFloatingElementRenderer::getPositionOffset),
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
