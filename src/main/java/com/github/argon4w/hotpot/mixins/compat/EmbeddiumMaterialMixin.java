package com.github.argon4w.hotpot.mixins.compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.embeddedt.embeddium.impl.render.chunk.terrain.DefaultTerrainRenderPasses;
import org.embeddedt.embeddium.impl.render.chunk.terrain.TerrainRenderPass;
import org.embeddedt.embeddium.impl.render.chunk.terrain.material.Material;
import org.embeddedt.embeddium.impl.render.chunk.terrain.material.parameters.AlphaCutoffParameter;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Material.class)
public class EmbeddiumMaterialMixin {
    @Mutable @Shadow @Final public AlphaCutoffParameter alphaCutoff;

    @Mutable @Shadow @Final public int packed;

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/embeddedt/embeddium/impl/render/chunk/terrain/material/parameters/MaterialParameters;pack(Lorg/embeddedt/embeddium/impl/render/chunk/terrain/material/parameters/AlphaCutoffParameter;Z)I"))
    public int wrapPackedParameters(AlphaCutoffParameter alphaCutoff, boolean useMipmaps, Operation<Integer> original, @Local(argsOnly = true) TerrainRenderPass pass) {
        return original.call(pass == DefaultTerrainRenderPasses.TRANSLUCENT ? AlphaCutoffParameter.ONE_TENTH : alphaCutoff, useMipmaps);
    }

    @WrapOperation(method = "<init>", at = @At(value = "FIELD", target = "Lorg/embeddedt/embeddium/impl/render/chunk/terrain/material/Material;alphaCutoff:Lorg/embeddedt/embeddium/impl/render/chunk/terrain/material/parameters/AlphaCutoffParameter;"))
    public void wrapAlphaCutoff(Material instance, AlphaCutoffParameter value, Operation<Void> original, @Local(argsOnly = true) TerrainRenderPass pass) {
        original.call(instance, pass == DefaultTerrainRenderPasses.TRANSLUCENT ? AlphaCutoffParameter.ONE_TENTH : value);
    }
}