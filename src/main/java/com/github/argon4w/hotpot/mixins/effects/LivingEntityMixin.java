package com.github.argon4w.hotpot.mixins.effects;

import com.github.argon4w.hotpot.HotpotModEntry;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Enemy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {
    @Shadow public abstract boolean hasEffect(Holder<MobEffect> pEffect);

    @Inject(method = "canFreeze", at = @At("RETURN"), cancellable = true)
    public void canFreeze(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!hasEffect(HotpotModEntry.HOTPOT_WARM) && cir.getReturnValue());
    }

    @Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("RETURN"), cancellable = true)
    public void canAttack(LivingEntity pTarget, CallbackInfoReturnable<Boolean> cir) {
        if (!(this instanceof Enemy)) {
            return;
        }

        if (!pTarget.hasEffect(HotpotModEntry.HOTPOT_SMELLY)) {
            return;
        }

        cir.setReturnValue(false);
    }

    @Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;)Z", at = @At("RETURN"), cancellable = true)
    public void canAttack(LivingEntity pLivingentity, TargetingConditions pCondition, CallbackInfoReturnable<Boolean> cir) {
        if (!(this instanceof Enemy)) {
            return;
        }

        if (!pLivingentity.hasEffect(HotpotModEntry.HOTPOT_SMELLY)) {
            return;
        }

        cir.setReturnValue(false);
    }
}
