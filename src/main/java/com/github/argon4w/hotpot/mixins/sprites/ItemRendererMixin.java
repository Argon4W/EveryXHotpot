package com.github.argon4w.hotpot.mixins.sprites;

import com.github.argon4w.hotpot.client.items.sprites.TintedBakedQuad;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Inject(method = "renderQuadList", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/color/item/ItemColors;getColor(Lnet/minecraft/world/item/ItemStack;I)I", shift = At.Shift.BY, by = 2), require = 0)
    public void renderQuadList(PoseStack pPoseStack, VertexConsumer pBuffer, List<BakedQuad> pQuads, ItemStack pItemStack, int pCombinedLight, int pCombinedOverlay, CallbackInfo ci, @Local(index = 11, name = "i") LocalIntRef localIntRef, @Local(index = 10, name = "bakedquad") BakedQuad bakedquad) {
        if (bakedquad instanceof TintedBakedQuad tintedBakedQuad) {
            localIntRef.set(tintedBakedQuad.getColor().toARGBInt());
        }
    }
}
