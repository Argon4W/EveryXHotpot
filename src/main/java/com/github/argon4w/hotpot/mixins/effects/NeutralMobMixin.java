package com.github.argon4w.hotpot.mixins.effects;

import com.github.argon4w.hotpot.HotpotModEntry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Enemy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NeutralMob.class)
public interface NeutralMobMixin {
    @Inject(method = "isAngryAt", at = @At("RETURN"), cancellable = true)
    default void isAngryAt(LivingEntity pTarget, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof Enemy) {
            return;
        }

        if (pTarget instanceof Sheep) {
            System.out.println("1111");
        }

        if (!pTarget.hasEffect(HotpotModEntry.HOTPOT_SMELLY)) {
            return;
        }

        cir.setReturnValue(true);
    }
}
