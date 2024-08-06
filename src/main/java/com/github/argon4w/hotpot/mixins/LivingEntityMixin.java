package com.github.argon4w.hotpot.mixins;

import com.github.argon4w.hotpot.HotpotModEntry;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract boolean hasEffect(Holder<MobEffect> p_21024_);

    @Inject(method = "canFreeze", at = @At("RETURN"), cancellable = true)
    public void canFreeze(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!hasEffect(HotpotModEntry.HOTPOT_WARM) && cir.getReturnValue());
    }
}
