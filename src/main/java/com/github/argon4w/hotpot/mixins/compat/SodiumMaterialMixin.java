package com.github.argon4w.hotpot.mixins.compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.DefaultTerrainRenderPasses;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.parameters.AlphaCutoffParameter;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(Material.class)
public class SodiumMaterialMixin {

    @Mutable @Shadow @Final public AlphaCutoffParameter alphaCutoff;
    @Mutable @Shadow @Final public int packed;

    @WrapOperation(method = "<init>", at = @At(
            value = "INVOKE",
            target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/material/parameters/MaterialParameters;pack(Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/material/parameters/AlphaCutoffParameter;Z)I"))
    public int wrapPackedParameters(
            AlphaCutoffParameter alphaCutoff,
            boolean useMipmaps,
            Operation<Integer> original,
            @Local(argsOnly = true) TerrainRenderPass pass) {
        return original.call(
                pass == DefaultTerrainRenderPasses.TRANSLUCENT ? AlphaCutoffParameter.ONE_TENTH : alphaCutoff,
                useMipmaps);
    }

    @WrapOperation(method = "<init>", at = @At(
            value = "FIELD",
            target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/material/Material;alphaCutoff:Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/material/parameters/AlphaCutoffParameter;"))
    public void wrapAlphaCutoff(
            Material instance,
            AlphaCutoffParameter value,
            Operation<Void> original,
            @Local(argsOnly = true) TerrainRenderPass pass) {
        original.call(
                instance,
                pass == DefaultTerrainRenderPasses.TRANSLUCENT ? AlphaCutoffParameter.ONE_TENTH : value);
    }
}
