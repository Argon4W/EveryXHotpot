package com.github.argon4w.hotpot.mixins;

import net.minecraft.client.renderer.texture.SpriteContents;

import net.minecraft.client.renderer.texture.SpriteTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpriteContents.class)
public class SpriteContentsMixin {
    private boolean isTickerCreated = false;

    @Inject(method = "createTicker", at = @At("RETURN"), cancellable = true)
    public void createTickerMixin(CallbackInfoReturnable<SpriteTicker> returnable) {
        if (isTickerCreated) {
            returnable.setReturnValue(null);
        } else {
            isTickerCreated = true;
        }
    }
}