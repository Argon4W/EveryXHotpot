package com.github.argon4w.hotpot.mixins.sections;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SectionCompiler.class)
public class SectionCompilerMixin {

    @WrapOperation(method = "getOrBeginLayer", at = @At(
            value = "FIELD",
            target = "Lcom/mojang/blaze3d/vertex/DefaultVertexFormat;BLOCK:Lcom/mojang/blaze3d/vertex/VertexFormat;",
            opcode = Opcodes.GETSTATIC))
    public VertexFormat wrapFormatBasedOnRenderType(
            Operation<VertexFormat> original,
            @Local(argsOnly = true) RenderType renderTypeRef) {
        return renderTypeRef.format;
    }

    @WrapOperation(method = "getOrBeginLayer", at = @At(
            value = "FIELD",
            target = "Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;QUADS:Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;",
            opcode = Opcodes.GETSTATIC))
    public VertexFormat.Mode wrapModeBasedOnRenderType(
            Operation<VertexFormat> original,
            @Local(argsOnly = true) RenderType renderTypeRef) {
        return renderTypeRef.mode;
    }
}
