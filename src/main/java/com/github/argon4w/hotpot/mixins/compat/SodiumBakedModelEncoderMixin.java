package com.github.argon4w.hotpot.mixins.compat;

import com.github.argon4w.hotpot.client.items.sprites.TintedBakedQuad;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView;
import net.caffeinemc.mods.sodium.client.render.immediate.model.BakedModelEncoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(BakedModelEncoder.class)
public class SodiumBakedModelEncoderMixin {
    @Inject(method = "writeQuadVertices(Lnet/caffeinemc/mods/sodium/api/vertex/buffer/VertexBufferWriter;Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/caffeinemc/mods/sodium/client/model/quad/ModelQuadView;III)V", at = @At("HEAD"), require = 0)
    private static void writeQuadVertices(@Coerce Object writer, PoseStack.Pose matrices, ModelQuadView quad, int color, int light, int overlay, CallbackInfo ci, @Local(index = 3, name = "color", argsOnly = true) LocalIntRef ref) {
        if (quad instanceof TintedBakedQuad tintedBakedQuad) {
            ref.set(tintedBakedQuad.getColor().toABGRInt());
        }
    }
}
